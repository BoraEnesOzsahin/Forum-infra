package com.ayrotek.forum.exception;

import com.ayrotek.forum.entity.ServerResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

// Add import for MissingRelationException
import com.ayrotek.forum.exception.MissingRelationException;

@ControllerAdvice  // Re-enabled for clean exception handling
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ServerResponse> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        ServerResponse errorResponse = new ServerResponse(false, 
            "[UserNotFoundException] " + ex.getMessage(), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IdMismatchException.class)
    public ResponseEntity<ServerResponse> handleIdMismatchException(IdMismatchException ex, WebRequest request) {
        ServerResponse errorResponse = new ServerResponse(false, 
            "[IdMismatchException] " + ex.getMessage(), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingRelationException.class)
    public ResponseEntity<ServerResponse> handleMissingRelationException(MissingRelationException ex, WebRequest request) {
        ServerResponse errorResponse = new ServerResponse(false, 
            "[MissingRelationException] " + ex.getMessage(), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ServerResponse> handleGlobalException(Exception ex, WebRequest request) {
        ServerResponse errorResponse = new ServerResponse(false, 
            "[" + ex.getClass().getSimpleName() + "] An unexpected error occurred: " + ex.getMessage(), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
