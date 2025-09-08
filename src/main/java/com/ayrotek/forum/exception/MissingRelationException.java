package com.ayrotek.forum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MissingRelationException extends RuntimeException {
    public MissingRelationException(String message) {
        super(message);
    }
}