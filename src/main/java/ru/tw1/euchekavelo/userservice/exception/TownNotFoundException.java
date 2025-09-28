package ru.tw1.euchekavelo.userservice.exception;

public class TownNotFoundException extends RuntimeException {

    public TownNotFoundException(String message) {
        super(message);
    }
}
