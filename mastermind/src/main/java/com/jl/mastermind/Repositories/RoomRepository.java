package com.jl.mastermind.Repositories;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;

import com.jl.mastermind.Models.Player;
import com.jl.mastermind.Models.Room;
import com.jl.mastermind.Models.RoomUpdate;

@Repository
public class RoomRepository {
    private Map<String, Room> roomMap = new ConcurrentHashMap<>();

    public RoomRepository() {
        roomMap.put("room1", new Room("Room1", new Player("Timmy"), 4, 10, false, false, "1234", null));
        roomMap.put("room2", new Room("Room2", new Player("Timmy"), 4, 10, false, false, "5678", null));
        roomMap.put("room3", new Room("Room3", new Player("Timmy"), 4, 10, false, false, "9012", null));
    }

    
    public Optional<Room> findByRoomName(String roomName) {
        return Optional.ofNullable(roomMap.get(roomName.toLowerCase()));
    }

    public boolean deleteRoom(String roomName) {
        if (roomMap.containsKey(roomName.toLowerCase())) {
            roomMap.remove(roomName.toLowerCase());
            return true;
        } else {
            return false;
        }
    }

    public Room updateRoom(String roomName, RoomUpdate roomUpdate, Room room) throws URISyntaxException {
        if (roomUpdate.getDifficulty() != null) {
            room.setDifficulty(roomUpdate.getDifficulty());
        }
        if (roomUpdate.getMaxGuesses() != null) {
            room.setMaxGuesses(roomUpdate.getMaxGuesses());
        }
        if (roomUpdate.getClosed() != null) {
            room.setClosed(roomUpdate.getClosed());
        }
        if (roomUpdate.getStarted() != null) {
            room.setStarted(roomUpdate.getStarted());
            if (room.isClosed() == false) {
                room.setClosed(true);
            }
        }
        if (roomUpdate.getMastercode() == true) {
            room.setMastercode(randomPatternGenerator(room.getDifficulty()));
        }
        roomMap.put(roomName.toLowerCase(), room);
        return room;
    }

    public Room createRoom(Room newRoom) {
       return roomMap.put(newRoom.getRoomName(), newRoom);

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

                System.out.println("Response Body:\n" + response.toString());

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

                    System.out.println("Extracted data:" + mastercodeJSON.toString());

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

    public Map<String, Room> getRoomMap() {
        return roomMap;
    }
}
