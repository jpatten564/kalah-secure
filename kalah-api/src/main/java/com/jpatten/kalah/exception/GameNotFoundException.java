package com.jpatten.kalah.exception;

public class GameNotFoundException extends RuntimeException {

    public GameNotFoundException(String message) {
        super(message);
    }
}
