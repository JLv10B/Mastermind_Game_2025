package com.jl.mastermind.repositories;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.jl.mastermind.entities.Room;

@Repository
public class RoomRepository {
    private Map<String, Room> roomMap = new ConcurrentHashMap<>();

    public RoomRepository() {
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
