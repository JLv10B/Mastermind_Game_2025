package com.jl.mastermind.Controllers;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jl.mastermind.Models.Room;
import com.jl.mastermind.Repositories.RoomRepository;

@RestController
@RequestMapping("/rooms")
public class RoomController {
    private final RoomRepository roomRepository;

    public RoomController(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @GetMapping()
    public Map<Integer, Room> getRoomMap() {
        return roomRepository.getRoomMap();
    }

    @GetMapping("/{id}")
    public Room getRoom(@PathVariable int id) {
        return roomRepository.findById(id);
    }
}
