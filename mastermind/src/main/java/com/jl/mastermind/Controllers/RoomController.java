package com.jl.mastermind.controllers;

import java.net.URISyntaxException;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jl.mastermind.dto.PlayerGuessDTO;
import com.jl.mastermind.dto.PlayerRoomViewDTO;
import com.jl.mastermind.dto.RoomCreationDTO;
import com.jl.mastermind.dto.RoomUpdateDTO;
import com.jl.mastermind.entities.Player;
import com.jl.mastermind.entities.PlayerGuess;
import com.jl.mastermind.entities.Room;
import com.jl.mastermind.services.RoomService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/rooms")
public class RoomController {
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }


    @GetMapping("/secret-all-rooms")
    public Map<String, Room> getRoomMap() {
        return roomService.getRoomMap();
    }


    @GetMapping("/{roomName}/secret")
    public ResponseEntity<Room> getRoom(@PathVariable String roomName) {
        Room room = roomService.getRoom(roomName.toLowerCase());
        return ResponseEntity.ok(room);
    }


    @GetMapping("/{roomName}")
    public ResponseEntity<PlayerRoomViewDTO> getRoom(@PathVariable String roomName, HttpSession session) {
        PlayerRoomViewDTO room = roomService.getRoomPublic(roomName.toLowerCase(), session);
        return ResponseEntity.ok(room);
    }


    @DeleteMapping("/{roomName}")
    public ResponseEntity<String> deleteRoom(@PathVariable String roomName, HttpSession session) {
        if (roomService.deleteRoom(roomName, session) == true) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }


    @PatchMapping("/{roomName}")
    public ResponseEntity<Room> updateRoom(@PathVariable String roomName, @RequestBody RoomUpdateDTO roomUpdate) throws URISyntaxException {
        Room updatedRoom = roomService.updateRoom(roomName, roomUpdate);
        return ResponseEntity.ok(updatedRoom);
    }


    @PostMapping("/create-room")
    public ResponseEntity<Room> createRoom(@Valid @RequestBody RoomCreationDTO roomCreationDTO, HttpSession session) throws URISyntaxException {
        Room newRoom = roomService.createRoom(roomCreationDTO, session);
        return ResponseEntity.status(HttpStatus.CREATED).body(newRoom);
    }


    @PostMapping("/{roomName}/reset-room")
    public ResponseEntity<PlayerRoomViewDTO> resetRoom(@PathVariable String roomName, @RequestBody RoomUpdateDTO roomUpdateDTO, HttpSession session) throws URISyntaxException {
        PlayerRoomViewDTO updatedRoom = roomService.resetRoom(roomName, roomUpdateDTO, session);
        return ResponseEntity.ok(updatedRoom);
    }

    
    @PostMapping("/{roomName}/submit-guess")
    public ResponseEntity<PlayerGuess> submitGuess(@PathVariable String roomName, @RequestBody PlayerGuessDTO playerGuess, HttpSession session) {
        PlayerGuess feedback = roomService.submitGuess(roomName, playerGuess, session);
        return ResponseEntity.ok(feedback);
    }
}
