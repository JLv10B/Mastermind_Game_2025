package com.jl.mastermind.repositories;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.jl.mastermind.entities.Player;
import com.jl.mastermind.entities.Room;

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

    public Room saveRoom(Room newRoom) {
       return roomMap.put(newRoom.getRoomName().toLowerCase(), newRoom);

    }

    public Map<String, Room> getRoomMap() {
        return roomMap;
    }
}
