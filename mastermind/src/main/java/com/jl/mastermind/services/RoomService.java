package com.jl.mastermind.services;

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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.jl.mastermind.dto.RoomCreationDTO;
import com.jl.mastermind.dto.RoomUpdateDTO;
import com.jl.mastermind.entities.Player;
import com.jl.mastermind.entities.PlayerGuess;
import com.jl.mastermind.entities.Room;
import com.jl.mastermind.exceptions.InsufficientPermissionsException;
import com.jl.mastermind.exceptions.NoUserFoundException;
import com.jl.mastermind.exceptions.ResourceNotFoundException;
import com.jl.mastermind.exceptions.RoomNameAlreadyExistsException;
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

    public boolean deleteRoom(String roomName, HttpSession session) {
        Optional<Room> roomOptional = roomRepository.findByRoomName(roomName.toLowerCase());
        if (roomOptional.isPresent()) {
            String clientUsernameLower = session.getAttribute("username").toString().toLowerCase();
            String hostUsernameLower = roomOptional.get().getHost().getUsername().toLowerCase();
            if (clientUsernameLower == hostUsernameLower) {
                return roomRepository.deleteRoom(roomName.toLowerCase());
            } else {
                throw new InsufficientPermissionsException("Insufficient permissions to perform this request");
            }
        } else {
            throw new ResourceNotFoundException(roomName + " not found");
        }
    }

    public Room updateRoom(String roomName, RoomUpdateDTO roomUpdate) throws URISyntaxException {
        Optional<Room> roomOptional = roomRepository.findByRoomName(roomName.toLowerCase());
        if (!roomOptional.isPresent()) {
            throw new ResourceNotFoundException(roomName + " not found");
        } else {
            Room updatedRoom = roomOptional.get();
            if (roomUpdate.getDifficulty() != null) {
                updatedRoom.setDifficulty(roomUpdate.getDifficulty());
            }
            if (roomUpdate.getMaxGuesses() != null) {
                updatedRoom.setMaxGuesses(roomUpdate.getMaxGuesses());
            }
            if (roomUpdate.getClosed() != null) {
                updatedRoom.setClosed(roomUpdate.getClosed());
            }
            if (roomUpdate.getStarted() != null) {
                updatedRoom.setStarted(roomUpdate.getStarted());
                if (updatedRoom.isClosed() == false) {
                    updatedRoom.setClosed(true);
                }
            }
            if (roomUpdate.getMastercode() == true) {
                updatedRoom.setMastercode(randomPatternGenerator(updatedRoom.getDifficulty()));
            }
            roomRepository.saveRoom(updatedRoom);
            return updatedRoom;
        }
    }

    public Room createRoom(RoomCreationDTO roomCreationDTO, HttpSession session) throws URISyntaxException {
        if (session.getAttribute("username") == null) {
            throw new NoUserFoundException("No username found, unable to create a room");
        }
        Optional<Room> roomOptional = roomRepository.findByRoomName(roomCreationDTO.getRoomName().toLowerCase());
        if (roomOptional.isPresent()) {
            throw new RoomNameAlreadyExistsException(roomCreationDTO.getRoomName() + " already exists");
        } else {
            String roomName = roomCreationDTO.getRoomName();
            int difficulty = roomCreationDTO.getDifficulty();
            int maxGuesses = roomCreationDTO.getMaxGuesses();
            boolean closed = roomCreationDTO.isClosed();
            Player host = playerService.getPlayerByName(session.getAttribute("username").toString().toLowerCase());
            String masterCode = randomPatternGenerator(difficulty);
            
            List<PlayerGuess> guessList = new ArrayList<>();
            Map<String, List<PlayerGuess>> participants = new HashMap<>();
            participants.put(host.getUsername(), guessList);

            Room newRoom = new Room(roomName, host, difficulty, maxGuesses, closed, false, masterCode, participants);
            roomRepository.saveRoom(newRoom);

            roomOptional = roomRepository.findByRoomName(newRoom.getRoomName().toLowerCase());
            if (roomOptional.isPresent()) {
                return newRoom;
            } else {
                throw new ResourceNotFoundException(newRoom.getRoomName() + " not found");
            }
        } 
    }

    public Room addParticipant(String roomName, String playerUsername) {
        Optional<Room> roomOptional = roomRepository.findByRoomName(roomName.toLowerCase());
        if (!roomOptional.isPresent()) {
            throw new ResourceNotFoundException(roomName + " not found");
        } else {
            Room room = roomOptional.get();
            Map<String, List<PlayerGuess>> participants = room.getParticipants();
            List<PlayerGuess> guessList = new ArrayList<>();
            participants.put(playerUsername, guessList);
            return room;
        } 
    }

    public Room removeParticipant(String roomName, String playerUsername) {
        Optional<Room> roomOptional = roomRepository.findByRoomName(roomName.toLowerCase());
        if (!roomOptional.isPresent()) {
            throw new ResourceNotFoundException(roomName + " not found");
        } else {
            Room room = roomOptional.get();
            Map<String, List<PlayerGuess>> participants = room.getParticipants();
            participants.remove(playerUsername);
            return room;
        } 
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
