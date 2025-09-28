package ru.tw1.euchekavelo.userservice.exception;

public class ResourceAccessDeniedException extends RuntimeException{

    public ResourceAccessDeniedException(String message) {
        super(message);
    }
}
