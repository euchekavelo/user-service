package ru.tw1.euchekavelo.userservice.exception;

public class UserGroupNotFoundException extends RuntimeException {

    public UserGroupNotFoundException(String message){
        super(message);
    }
}
