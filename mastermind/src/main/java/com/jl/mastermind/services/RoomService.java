package com.jl.mastermind.services;

import static com.jl.mastermind.util.AppConstants.RoomParameters.LARGEST_DIGIT;
import static com.jl.mastermind.util.AppConstants.RoomParameters.MAX_DIFFICULTY;
import static com.jl.mastermind.util.AppConstants.RoomParameters.MAX_GUESSES;
import static com.jl.mastermind.util.AppConstants.RoomParameters.MIN_DIFFICULTY;
import static com.jl.mastermind.util.AppConstants.RoomParameters.SMALLEST_DIGIT;

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
    private final PlayerScoreService playerScoreService;

    public RoomService(RoomRepository roomRepository, PlayerService playerService, PlayerScoreService playerScoreService) {
        this.roomRepository = roomRepository;
        this.playerService = playerService;
        this.playerScoreService = playerScoreService;
    }

    /**
     * Retrieves all rooms from the repository.
     * 
     * @return a Map containing all rooms, where the key is the room identifier
     *         and the value is the Room object
     */
    public Map<String, Room> getRoomMap() {
        return roomRepository.getRoomMap();
    }

    /**
     * Retrieves a room by its name.
     * 
     * <p>The room name lookup is case-insensitive as it's converted to lowercase
     * before querying the repository.</p>
     * 
     * @param roomName the name of the room to retrieve (case-insensitive)
     * @return the Room object associated with the given room name
     * @throws ResourceNotFoundException if no room is found with the specified name
     */
    public Room getRoom(String roomName) {
        Optional<Room> roomOptional = roomRepository.findByRoomName(roomName.toLowerCase());
        if (roomOptional.isPresent()) {
            Room room = roomOptional.get();
            return room;
        } else {
            throw new ResourceNotFoundException(roomName + " not found");
        }
    }

    /**
     * Retrieves a public view of a room for the host player.
     * 
     * <p>This method validates that the requesting user is the host of the room
     * before returning the room data. It calculates remaining guesses and
     * provides a sanitized view of the room state.</p>
     * 
     * @param roomName the name of the room to retrieve
     * @param session the HTTP session containing the current user's information
     * @return a PlayerRoomViewDTO containing the room's public information
     * @throws ResourceNotFoundException if no room is found with the specified name
     * @throws InsufficientPermissionsException if the requesting user is not the room host
     */
    public PlayerRoomViewDTO getRoomPublic(String roomName, HttpSession session) {
        Optional<Room> roomOptional = roomRepository.findByRoomName(roomName.toLowerCase());
        if (roomOptional.isPresent()) {
            Room room = roomOptional.get();
            if (room.getHost().getUsername().equalsIgnoreCase(session.getAttribute("username").toString())) {
                int remainingGuesses = MAX_GUESSES - room.getGuessList().size();
                return new PlayerRoomViewDTO(roomName, room.getHost(), room.getDifficulty(), room.isStarted(), room.isCompleted(), remainingGuesses, room.getGuessList());
            } else {
                throw new InsufficientPermissionsException("This is not your room");
            }
        } else {
            throw new ResourceNotFoundException(roomName + " not found");
        }
    }

   /**
     * Deletes a room if the requesting user is the host.
     * 
     * <p>This method performs permission validation to ensure only the room host
     * can delete the room. The comparison is case-insensitive.</p>
     * 
     * @param roomName the name of the room to delete
     * @param session the HTTP session containing the current user's information
     * @return {@code true} if the room was successfully deleted, {@code false} otherwise
     * @throws ResourceNotFoundException if no room is found with the specified name
     * @throws InsufficientPermissionsException if the requesting user is not the room host
     */
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

    /**
     * Updates room settings such as difficulty and start status.
     * 
     * <p>This method validates that the game hasn't started before allowing modifications.
     * It supports updating difficulty (within valid bounds) and the started flag.</p>
     * 
     * @param roomName the name of the room to update
     * @param roomUpdateDTO the DTO containing the updated room settings
     * @return the updated Room object
     * @throws ResourceNotFoundException if no room is found with the specified name
     * @throws InsufficientPermissionsException if the game has already started
     * @throws InvalidInputException if difficulty is outside valid bounds
     * @throws URISyntaxException if there are issues with URI construction (unused in current implementation)
     */
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

    /**
     * Creates a new room or retrieves an existing one if the user is the host.
     * 
     * <p>This method performs several validations:</p>
     * <ul>
     *   <li>Ensures user is logged in</li>
     *   <li>Validates room name length (5-100 characters)</li>
     *   <li>Checks if room already exists and validates ownership</li>
     *   <li>Generates a random master code for new rooms</li>
     * </ul>
     * 
     * @param roomCreationDTO the DTO containing room creation parameters
     * @param session the HTTP session containing the current user's information
     * @return the existing Room if found and owned by user, or the newly created Room
     * @throws NoUserFoundException if no username is found in the session
     * @throws InvalidInputException if the room name length is invalid
     * @throws InsufficientPermissionsException if trying to access another user's room
     * @throws URISyntaxException if there are issues with Random.org API URI construction
     */
    public Room getOrCreateRoom(RoomCreationDTO roomCreationDTO, HttpSession session) throws URISyntaxException {
        if (session.getAttribute("username") == null) {
            throw new NoUserFoundException("No username found, unable to create a room");
        }
        if (roomCreationDTO.getRoomName().length() < 5 || roomCreationDTO.getRoomName().length() > 100) {
            throw new InvalidInputException("Room name not long enough");
        }
        Optional<Room> roomOptional = roomRepository.findByRoomName(roomCreationDTO.getRoomName().toLowerCase());
        if (roomOptional.isPresent()) {
            Room foundRoom = roomOptional.get();
            if (session.getAttribute("username").toString() != foundRoom.getHost().getUsername()) {
                throw new InsufficientPermissionsException("You do not have permission to enter the room");
            }
            return foundRoom;
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

    /**
     * Resets a room to its initial state with a new master code.
     * 
     * <p>This method allows the room host to reset the game, generating a new
     * master code, clearing all guesses, and resetting game state flags.</p>
     * 
     * @param roomName the name of the room to reset
     * @param roomUpdate the DTO containing reset parameters (must set resetMastercode to true)
     * @param session the HTTP session containing the current user's information
     * @return a PlayerRoomViewDTO of the reset room
     * @throws ResourceNotFoundException if no room is found with the specified name
     * @throws InsufficientPermissionsException if the requesting user is not the room host
     * @throws InvalidInputException if the reset parameters are invalid
     * @throws URISyntaxException if there are issues with Random.org API URI construction
     */
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
            return new PlayerRoomViewDTO(roomName, updatedRoom.getHost(), updatedRoom.getDifficulty(), updatedRoom.isStarted(), updatedRoom.isCompleted(), MAX_GUESSES, guessList);
        } else {
            throw new InvalidInputException("Unable to process input");
        }
        
    }

    /**
     * Processes a player's guess and returns feedback.
     * 
     * <p>This method performs extensive validation and game logic:</p>
     * <ul>
     *   <li>Validates user permissions and game state</li>
     *   <li>Validates guess format (numeric, correct length, valid digits)</li>
     *   <li>Processes the guess against the master code</li>
     *   <li>Updates game completion status</li>
     *   <li>Updates player scores on successful completion</li>
     * </ul>
     * 
     * @param roomName the name of the room where the guess is being made
     * @param playerGuessDTO the DTO containing the player's guess
     * @param session the HTTP session containing the current user's information
     * @return a PlayerGuess object containing the guess and feedback
     * @throws ResourceNotFoundException if no room is found with the specified name
     * @throws InsufficientPermissionsException if the requesting user is not the room host
     * @throws GameNotStartedException if the game hasn't started or is already completed
     * @throws InvalidInputException if the guess format is invalid
     */
    public PlayerGuess submitGuess(String roomName, PlayerGuessDTO playerGuessDTO, HttpSession session) {
        Room room = getRoom(roomName);
        if (session.getAttribute("username").toString() != room.getHost().getUsername()) {
            throw new InsufficientPermissionsException("You aren't the host");
        }
        if (!room.isStarted()) {
            throw new GameNotStartedException("Game has not started yet");
        }
        if (room.isCompleted()) {
            throw new GameNotStartedException("Game completed, reset the game to play again");
        }
        
        String playerGuessString = playerGuessDTO.getPlayerGuess();
        
        if (playerGuessString.length() == 0) {
            throw new InvalidInputException("You must enter a guess");
        }
        if (!playerGuessString.matches("\\d+")) {
            throw new InvalidInputException("Please submit only numbers");
        }
        if (playerGuessString.length() != room.getDifficulty()) {
            throw new InvalidInputException("Please submit the correct number of digits");
        }
        
        List<Integer> playerGuessArray = new ArrayList<>();
        for (char x : playerGuessString.toCharArray()) {
            playerGuessArray.add(Character.getNumericValue(x));
        }

        for (int i=0; i<playerGuessArray.size(); i++) {
            if (playerGuessArray.get(i) < SMALLEST_DIGIT || playerGuessArray.get(i) > LARGEST_DIGIT ) {
                throw new InvalidInputException("Please enter digits between " + SMALLEST_DIGIT + " and " + LARGEST_DIGIT);
            }
        }
        
        List<PlayerGuess> guessList = room.getGuessList();
        int currentGuessCount = MAX_GUESSES - guessList.size();
        int remainingGuesses = currentGuessCount - 1;
        PlayerGuess playerGuess = createGuess(playerGuessArray, playerGuessString, room.getMastercode(), remainingGuesses);
        guessList.add(playerGuess);
        
        if (playerGuess.isCorrectGuess()) {
            room.setCompleted(true);
            playerScoreService.updatePlayerScore(room.getHost().getUsername(), room.getDifficulty());
        }

        if(remainingGuesses <= 0) {
            room.setCompleted(true);
        }

        return playerGuess;
        
    }

    /**
     * Creates a PlayerGuess object with feedback based on the guess and master code.
     * 
     * <p>This method implements the core Mastermind logic:</p>
     * <ul>
     *   <li>Calculates exact matches (correct digit in correct position)</li>
     *   <li>Calculates total matching numbers (correct digits in any position)</li>
     *   <li>Generates appropriate feedback messages</li>
     *   <li>Determines if the guess is correct</li>
     * </ul>
     * 
     * @param playerGuessArray the player's guess as a list of integers
     * @param playerGuessString the player's guess as a string
     * @param masterCode the master code to compare against
     * @param remainingGuesses the number of guesses remaining after this one
     * @return a PlayerGuess object containing the guess, correctness, and feedback
     */
    public PlayerGuess createGuess(List<Integer> playerGuessArray, String playerGuessString, String masterCode, int remainingGuesses) { 
        String feedback;
        int matchingNumbers = 0;
        int exactMatch= 0;
        
        List<Integer> MasterCodeArray = new ArrayList<>();
        for (char y : masterCode.toCharArray()) {
            MasterCodeArray.add(Character.getNumericValue(y));
        }

        for (int i=0; i<MasterCodeArray.size(); i++) {
            if (playerGuessArray.get(i) == (MasterCodeArray.get(i))) {
                exactMatch++;
            }
        }

        Map<Integer, Integer> frequencyMap = new HashMap<>();
        for (int i : MasterCodeArray) {
            frequencyMap.put(i, frequencyMap.getOrDefault(i, 0) + 1);
        }
        for (int i : playerGuessArray) {
            if (frequencyMap.containsKey(i) && frequencyMap.get(i) > 0) {
                matchingNumbers++;
                frequencyMap.put(i, frequencyMap.get(i) - 1);
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

    /**
     * Converts a Room entity to a PlayerRoomViewDTO.
     * 
     * <p>This method creates a sanitized view of the room suitable for client consumption,
     * calculating remaining guesses and excluding sensitive information like the master code.</p>
     * 
     * @param room the Room entity to convert
     * @return a PlayerRoomViewDTO containing the room's public information
     */
    public PlayerRoomViewDTO roomToPlayerView(Room room) {
        int remainingGuesses = MAX_GUESSES - room.getGuessList().size();
        return new PlayerRoomViewDTO(room.getRoomName(), room.getHost(), room.getDifficulty(), room.isStarted(), room.isCompleted(), remainingGuesses, room.getGuessList());
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
