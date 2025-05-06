package ru.skillbox.userservice.exception;

public class ForbiddenAccessException extends Exception {

    public ForbiddenAccessException(String message) {
        super(message);
    }
}
