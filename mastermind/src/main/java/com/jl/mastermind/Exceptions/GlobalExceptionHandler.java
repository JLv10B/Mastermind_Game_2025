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

    @ExceptionHandler({NameAlreadyExistsException.class})
    public ResponseEntity<Object> handleRoomNameAlreadyExistsException(NameAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatusCode.valueOf(409)).body(e.getMessage());
    }

    @ExceptionHandler({NoUserFoundException.class})
    public ResponseEntity<Object> handleNoUserFoundException(NoUserFoundException e) {
        return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(e.getMessage());
    }

    @ExceptionHandler({InsufficientPermissionsException.class})
    public ResponseEntity<Object> handleInsufficientPermissionsException(InsufficientPermissionsException e) {
        return ResponseEntity.status(HttpStatusCode.valueOf(403)).body(e.getMessage());
    }

    @ExceptionHandler({InvalidInputException.class})
    public ResponseEntity<Object> handleInvalidInputException(InvalidInputException e) {
        return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(e.getMessage());
    }

    @ExceptionHandler({GameNotStartedException.class})
    public ResponseEntity<Object> handleGameNotStartedException(GameNotStartedException e) {
        return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(e.getMessage());
    }

}
