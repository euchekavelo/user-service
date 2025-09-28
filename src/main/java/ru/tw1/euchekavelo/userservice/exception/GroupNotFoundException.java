package ru.tw1.euchekavelo.userservice.exception;

public class GroupNotFoundException extends RuntimeException {

    public GroupNotFoundException(String message) {
        super(message);
    }
}
