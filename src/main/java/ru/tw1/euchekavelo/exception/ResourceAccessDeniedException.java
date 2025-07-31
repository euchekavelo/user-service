package ru.tw1.euchekavelo.exception;

public class ResourceAccessDeniedException extends RuntimeException{

    public ResourceAccessDeniedException(String message) {
        super(message);
    }
}
