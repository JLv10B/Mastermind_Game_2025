package com.jl.mastermind.Controllers;

import java.lang.classfile.ClassFile.Option;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

import com.jl.mastermind.Exceptions.ResourceNotFoundException;
import com.jl.mastermind.Exceptions.RoomNameAlreadyExistsException;
import com.jl.mastermind.Models.Player;
import com.jl.mastermind.Models.Room;
import com.jl.mastermind.Models.RoomUpdate;
import com.jl.mastermind.Repositories.RoomRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/rooms")
public class RoomController {
    private final RoomRepository roomRepository;

    public RoomController(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @GetMapping()
    public Map<String, Room> getRoomMap() {
        return roomRepository.getRoomMap();
    }

    @GetMapping("/{roomName}")
    public ResponseEntity<Room> getRoom(@PathVariable String roomName) {
        Optional<Room> roomOptional = roomRepository.findByRoomName(roomName.toLowerCase());
        if (roomOptional.isPresent()) {
            Room room = roomOptional.get();
            return ResponseEntity.ok(room);
        } else {
            throw new ResourceNotFoundException(roomName + " not found");
        }
    }

    @DeleteMapping("/{roomName}")
    public ResponseEntity<String> deleteRoom(@PathVariable String roomName) {
        boolean deleted;
        Optional<Room> roomOptional = roomRepository.findByRoomName(roomName.toLowerCase());
        if (roomOptional.isPresent()) {
            deleted = roomRepository.deleteRoom(roomName.toLowerCase());
        } else {
            throw new ResourceNotFoundException(roomName + " not found");
        }
        if (deleted == true) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PatchMapping("/{roomName}")
    public ResponseEntity<Room> updateRoom(@PathVariable String roomName, @RequestBody RoomUpdate roomUpdate) throws URISyntaxException {
        Optional<Room> roomOptional = roomRepository.findByRoomName(roomName.toLowerCase());
        if (roomOptional.isPresent()) {
            Room updatedRoom = roomOptional.get();
            updatedRoom = roomRepository.updateRoom(roomName, roomUpdate, updatedRoom);
            return ResponseEntity.ok(updatedRoom);
        } else {
            throw new ResourceNotFoundException(roomName + " not found");
        }
    }

    @PostMapping("/rooms/create-room")
    public ResponseEntity<Room> createRoom(@Valid @RequestBody Room room) {
        Optional<Room> roomOptional = roomRepository.findByRoomName(room.getRoomName().toLowerCase());
        if (roomOptional.isPresent()) {
            throw new RoomNameAlreadyExistsException(room.getRoomName() + " already exists");
        } else {
            roomRepository.createRoom(room);
            roomOptional = roomRepository.findByRoomName(room.getRoomName().toLowerCase());
            if (roomOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.CREATED).body(room);
            } else {
                return ResponseEntity.internalServerError().build();
            }
        } 
    }

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
