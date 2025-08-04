package com.jl.mastermind.controllers;

import java.net.URISyntaxException;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jl.mastermind.dto.RoomUpdateDTO;
import com.jl.mastermind.entities.Room;
import com.jl.mastermind.services.RoomService;


@RestController
@RequestMapping("/rooms")
public class RoomController {
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping()
    public Map<String, Room> getRoomMap() {
        return roomService.getRoomMap();
    }

    @GetMapping("/{roomName}")
    public ResponseEntity<Room> getRoom(@PathVariable String roomName) {
        Room room = roomService.getRoom(roomName.toLowerCase());
        return ResponseEntity.ok(room);
    }

    @DeleteMapping("/{roomName}")
    public ResponseEntity<String> deleteRoom(@PathVariable String roomName) {
        if (roomService.deleteRoom(roomName) == true) {
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

    // @PostMapping("/rooms/create-room")
    // public ResponseEntity<Room> createRoom(@Valid @RequestBody Room room) {
    //     Optional<Room> roomOptional = roomRepository.findByRoomName(room.getRoomName().toLowerCase());
    //     if (roomOptional.isPresent()) {
    //         throw new RoomNameAlreadyExistsException(room.getRoomName() + " already exists");
    //     } else {
    //         roomRepository.createRoom(room);
    //         roomOptional = roomRepository.findByRoomName(room.getRoomName().toLowerCase());
    //         if (roomOptional.isPresent()) {
    //             return ResponseEntity.status(HttpStatus.CREATED).body(room);
    //         } else {
    //             return ResponseEntity.internalServerError().build();
    //         }
    //     } 
    // }

    // @PostMapping("/{roomName}/Participants")
    // public ResponseEntity<Map> addParticipant(@PathVariable String roomName, @RequestBody String joiningPlayerUsername) {
    //     Optional<Room> roomOptional = roomRepository.findByRoomName(roomName.toLowerCase());
    //     if (roomOptional.isPresent()) {
    //         Room updatedRoom = roomOptional.get();
    //         updatedRoom = roomRepository.updateRoom(roomName, roomUpdate, updatedRoom);
    //         return ResponseEntity.ok(updatedRoom);
    //     } else {
    //         throw new ResourceNotFoundException(roomName + " not found");
    //     } 
    // }
}
