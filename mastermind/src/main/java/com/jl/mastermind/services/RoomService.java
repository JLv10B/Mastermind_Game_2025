package com.jl.mastermind.services;

import static com.jl.mastermind.util.AppConstants.RoomParameters.MAX_DIFFICULTY;
import static com.jl.mastermind.util.AppConstants.RoomParameters.MAX_GUESSES;
import static com.jl.mastermind.util.AppConstants.RoomParameters.MIN_DIFFICULTY;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.jl.mastermind.dto.*;
import com.jl.mastermind.entities.*;
import com.jl.mastermind.exceptions.*;
import com.jl.mastermind.repositories.RoomRepository;

import jakarta.servlet.http.HttpSession;

@Service
public class RoomService {

    private final PlayerService playerService;
    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository, PlayerService playerService) {
        this.roomRepository = roomRepository;
        this.playerService = playerService;
    }

    public Map<String, Room> getRoomMap() {
        return roomRepository.getRoomMap();
    }


    public Room getRoom(String roomName) {
        Optional<Room> roomOptional = roomRepository.findByRoomName(roomName.toLowerCase());
        if (roomOptional.isPresent()) {
            Room room = roomOptional.get();
            return room;
        } else {
            throw new ResourceNotFoundException(roomName + " not found");
        }
    }


    public PlayerRoomViewDTO getRoomPublic(String roomName, HttpSession session) {
        Optional<Room> roomOptional = roomRepository.findByRoomName(roomName.toLowerCase());
        if (roomOptional.isPresent()) {
            Room room = roomOptional.get();
            if (room.getHost().getUsername().equalsIgnoreCase(session.getAttribute("username").toString())) {
                return new PlayerRoomViewDTO(roomName, room.getHost(), room.getDifficulty(), room.isStarted(), room.isCompleted(), room.getGuessList());
            } else {
                throw new InsufficientPermissionsException("This is not your room");
            }
        } else {
            throw new ResourceNotFoundException(roomName + " not found");
        }
    }


    public boolean deleteRoom(String roomName, HttpSession session) {
        Optional<Room> roomOptional = roomRepository.findByRoomName(roomName.toLowerCase());
        if (roomOptional.isPresent()) {
            String clientUsernameLower = session.getAttribute("username").toString().toLowerCase();
            String hostUsernameLower = roomOptional.get().getHost().getUsername().toLowerCase();
            if (clientUsernameLower.equalsIgnoreCase(hostUsernameLower)) {
                return roomRepository.deleteRoom(roomName.toLowerCase());
            } else {
                throw new InsufficientPermissionsException("Insufficient permissions to perform this request");
            }
        } else {
            throw new ResourceNotFoundException(roomName + " not found");
        }
    }


    private boolean adminDeleteRoom(String roomName) {
        Optional<Room> roomOptional = roomRepository.findByRoomName(roomName.toLowerCase());
        if (roomOptional.isPresent()) {
            return roomRepository.deleteRoom(roomName.toLowerCase());
        } else {
            throw new ResourceNotFoundException(roomName + " not found");
        }
    }


    public Room updateRoom(String roomName, RoomUpdateDTO roomUpdateDTO) throws URISyntaxException {
        Room updatedRoom = getRoom(roomName);
        if (updatedRoom.isStarted() == true) {
            throw new InsufficientPermissionsException("You can't modify the game, it has already started");
        }
        if (roomUpdateDTO.getDifficulty() != null) {
            if (roomUpdateDTO.getDifficulty() >= MIN_DIFFICULTY && roomUpdateDTO.getDifficulty() <= MAX_DIFFICULTY) {
                updatedRoom.setDifficulty(roomUpdateDTO.getDifficulty());
            } else {
                throw new InvalidInputException(String.format("Difficulty out of bounds, must be %d-%d", MIN_DIFFICULTY, MAX_DIFFICULTY));
            }
        } 
        if (roomUpdateDTO.getStarted() != null) {
            updatedRoom.setStarted(roomUpdateDTO.getStarted());
        }
        return roomRepository.saveRoom(updatedRoom);
    }


    public Room createRoom(RoomCreationDTO roomCreationDTO, HttpSession session) throws URISyntaxException {
        if (session.getAttribute("username") == null) {
            throw new NoUserFoundException("No username found, unable to create a room");
        }
        Optional<Room> roomOptional = roomRepository.findByRoomName(roomCreationDTO.getRoomName().toLowerCase());
        if (roomOptional.isPresent()) {
            throw new NameAlreadyExistsException(roomCreationDTO.getRoomName() + " already exists");
        } else {
            String roomName = roomCreationDTO.getRoomName();
            int difficulty = roomCreationDTO.getDifficulty();
            Player host = playerService.getPlayerByName(session.getAttribute("username").toString().toLowerCase());
            String masterCode = randomPatternGenerator(difficulty);
            
            List<PlayerGuess> guessList = new ArrayList<>();

            Room newRoom = new Room(roomName, host, difficulty, false, false, masterCode, guessList);
            roomRepository.saveRoom(newRoom);
            return newRoom;
        } 
    }


    public PlayerRoomViewDTO resetRoom(String roomName, RoomUpdateDTO roomUpdate, HttpSession session) throws URISyntaxException {
        Room updatedRoom = getRoom(roomName);
        if (!session.getAttribute("username").toString().equalsIgnoreCase(updatedRoom.getHost().getUsername())) {
            throw new InsufficientPermissionsException("Only the host can reset the room");
        }
        if (roomUpdate.getResetMastercode() != null && roomUpdate.getResetMastercode() == true) {
            updatedRoom.setMastercode(randomPatternGenerator(updatedRoom.getDifficulty()));
            updatedRoom.setStarted(false);
            updatedRoom.setCompleted(false);
            List<PlayerGuess> guessList = updatedRoom.getGuessList();
            guessList.clear();
            roomRepository.saveRoom(updatedRoom);
            return new PlayerRoomViewDTO(roomName, updatedRoom.getHost(), updatedRoom.getDifficulty(), updatedRoom.isStarted(), updatedRoom.isCompleted(), guessList);
        } else {
            throw new InvalidInputException("Unable to process input");
        }
        
    }


    public PlayerGuess submitGuess(String roomName, PlayerGuessDTO playerGuessDTO, HttpSession session) {
        String playerGuessString = playerGuessDTO.getPlayerGuess();
        Room room = getRoom(roomName);
        if (!room.isStarted()) {
            throw new GameNotStartedException("Game has not started yet");
        }
        if (room.isCompleted()) {
            return new PlayerGuess(playerGuessString, false, "Game completed, reset the game to play again", 0);
        }
        if (playerGuessString.length() == 0) {
            throw new InvalidInputException("You must enter a guess");
        }
        if (!playerGuessString.matches("\\d+")) {
            throw new InvalidInputException("Please submit only numbers");
        }
        if (playerGuessDTO == null || playerGuessString.length() != room.getDifficulty()) {
            throw new InvalidInputException("Please submit the correct number of digits");
        }
        List<PlayerGuess> guessList = room.getGuessList();
        int currentGuessCount = MAX_GUESSES - guessList.size();
        if(currentGuessCount <= 0) {
            return new PlayerGuess(playerGuessString, false, "No guesses remaining, reset the game to play again", 0);
        }
        int remainingGuesses = currentGuessCount - 1;
        PlayerGuess playerGuess = createGuess(playerGuessString, room.getMastercode(), remainingGuesses);
        guessList.add(playerGuess);

        if (playerGuess.isCorrectGuess()) {
            room.setCompleted(true);
        }

        return playerGuess;
    }


private PlayerGuess createGuess(String playerGuessString, String masterCode, int remainingGuesses) { 
    String feedback;
    int matchingNumbers = 0;
    int exactMatch= 0;

    List<Character> tempPlayerGuessList = playerGuessString.chars().mapToObj(c -> (char) c).collect(Collectors.toList());
    List<Character> tempMasterCodeList = masterCode.chars().mapToObj(c -> (char) c).collect(Collectors.toList());

    for (int i=0; i<playerGuessString.length(); i++) {
        if (tempPlayerGuessList.get(i).equals(tempMasterCodeList.get(i))) {
            exactMatch++;
        }
    }

    Map<Character, Integer> frequencyMap = new HashMap<>();
    for (char c : tempMasterCodeList) {
        frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
    }
    for (char c : tempPlayerGuessList) {
        if (frequencyMap.containsKey(c) && frequencyMap.get(c) > 0) {
            matchingNumbers++;
            frequencyMap.put(c, frequencyMap.get(c) - 1);
        }
    }

    String locationString = "location";
    if (exactMatch > 1) {
        locationString = "locations";
    }

    String guessesString = "guesses";
    if (remainingGuesses == 1) {
        guessesString = "guess";
    }

    String gamestateString;
    boolean correctGuess = false;
    if (exactMatch == masterCode.length()) {
        correctGuess = true;
        gamestateString = "Congratulations, you win!";
    } else if (remainingGuesses <= 0) {
        gamestateString = "Sorry no more guesses left, you lose. Want to play again?";
    } else {
        gamestateString = "Oops, not quite. Try again!";
    }

    feedback = String.format("You guessed %s. You got %d correct with %d in the correct %s. You have %d %s remaining. %s", playerGuessString, matchingNumbers, exactMatch, locationString, remainingGuesses, guessesString, gamestateString);
    return new PlayerGuess(playerGuessString, correctGuess, feedback, remainingGuesses);
}
    

public String randomPatternGenerator(int difficulty) throws URISyntaxException {
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        BufferedReader reader = null;
        StringBuilder response = new StringBuilder();
        String mastercode = "";
        
        try {
            URI uri = new URI("https://api.random.org/json-rpc/4/invoke");
            URL url = uri.toURL();
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("User-Agent", "Mastermind Server");

            String jsonPayLoad = String.format("""
                {
                    "jsonrpc": "2.0",
                    "method": "generateIntegers",
                    "params": {
                        "apiKey": "6802e62a-b150-4821-ab25-8f0c637480fb",
                        "n": %d,
                        "min": 0,
                        "max": 7,
                        "replacement": false
                    },
                    "id": 1
                }
                """, difficulty);

            outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(jsonPayLoad);
            outputStream.flush();

            int responseCode = connection.getResponseCode();

            if (responseCode >= 200 && responseCode < 300) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                try {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    JSONObject result = jsonObject.getJSONObject("result");
                    JSONObject random = result.getJSONObject("random");
                    JSONArray mastercodeJSON = random.getJSONArray("data");
                    
                    StringBuilder sb = new StringBuilder("");
                    for (int i=0; i<mastercodeJSON.length(); i++) {
                        sb.append(mastercodeJSON.getString(i));
                    }
                    mastercode = sb.toString();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.err.println("Request failed with response code: " + responseCode);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null)
                    reader.close();
                if (outputStream != null)
                    outputStream.close();
                if (connection != null)
                    connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mastercode;
    }
}
