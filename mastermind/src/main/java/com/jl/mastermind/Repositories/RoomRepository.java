package com.jl.mastermind.Repositories;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Repository;

import com.jl.mastermind.Models.Player;
import com.jl.mastermind.Models.Room;

@Repository
public class RoomRepository {
    private Map<Integer, Room> roomMap = new ConcurrentHashMap<>();

    public RoomRepository() {
        roomMap.put(1, new Room(1, new Player("Timmy"), 4, 10, false, false, "1234", null));
        roomMap.put(2, new Room(2, new Player("Timmy"), 4, 10, false, false, "5678", null));
        roomMap.put(3, new Room(3, new Player("Timmy"), 4, 10, false, false, "9012", null));
    }

    

    public Room findById(int id) {
        return roomMap.get(id);
    }

    public Map<Integer, Room> getRoomMap() {
        return roomMap;
    }
}
