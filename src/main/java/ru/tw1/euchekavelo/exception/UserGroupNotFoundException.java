package ru.tw1.euchekavelo.exception;

public class UserGroupNotFoundException extends RuntimeException {

    public UserGroupNotFoundException(String message){
        super(message);
    }
}
