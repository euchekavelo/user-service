package ru.tw1.euchekavelo.userservice.exception;

public class UserSubscriptionNotFoundException extends RuntimeException {

    public UserSubscriptionNotFoundException(String message) {
        super(message);
    }
}
