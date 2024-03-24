package ru.skillbox.userservice.exception.enums;

public enum ExceptionMessage {

    GROUP_NOT_FOUND_EXCEPTION_MESSAGE("The group with the specified ID was not found."),
    TOWN_NOT_FOUND_EXCEPTION_MESSAGE("The town with the specified ID was not found."),
    USER_NOT_FOUND_EXCEPTION_MESSAGE("The user with the specified ID was not found.");

    private final String exceptionText;

    ExceptionMessage(String exceptionText) {
        this.exceptionText = exceptionText;
    }

    public String getExceptionMessage() {
        return exceptionText;
    }
}
