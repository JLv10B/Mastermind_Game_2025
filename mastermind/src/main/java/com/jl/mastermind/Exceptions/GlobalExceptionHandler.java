package com.jl.mastermind.exceptions;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ResourceNotFoundException.class})
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(e.getMessage());
    }

    @ExceptionHandler({UsernameAlreadyExistsException.class})
    public ResponseEntity<Object> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatusCode.valueOf(409)).body(e.getMessage());
    }

    @ExceptionHandler({RoomNameAlreadyExistsException.class})
    public ResponseEntity<Object> handleRoomNameAlreadyExistsException(RoomNameAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatusCode.valueOf(409)).body(e.getMessage());
    }

}
