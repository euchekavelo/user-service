package ru.skillbox.userservice.exception.enums;

public enum ExceptionMessage {

    GROUP_NOT_FOUND_EXCEPTION_MESSAGE("The group with the specified ID was not found."),
    TOWN_NOT_FOUND_EXCEPTION_MESSAGE("The town with the specified ID was not found."),
    USER_NOT_FOUND_EXCEPTION_MESSAGE("The user with the specified ID was not found."),
    USER_SUBSCRIPTION_EXCEPTION_MESSAGE("The user cannot subscribe to himself."),
    SOURCE_USER_NOT_FOUND_EXCEPTION_MESSAGE("The source user with the specified ID was not found."),
    DESTINATION_USER_NOT_FOUND_EXCEPTION_MESSAGE("The destination user with the specified ID was not found."),
    USER_SUBSCRIPTION_ALREADY_EXISTS_EXCEPTION_MESSAGE("A subscription to the destination user already exists."),
    USER_UNSUBSCRIBE_HIMSELF_EXCEPTION_MESSAGE("The user cannot unsubscribe to himself."),
    USER_SUBSCRIPTION_NOT_FOUND_EXCEPTION_MESSAGE("No subscription has been identified in " +
            "relation to the destination user."),
    IMPOSSIBLE_REMOVING_USER_GROUP_EXCEPTION_MESSAGE("Cannot remove a user from a group."),
    EMPTY_FILE_EXCEPTION_MESSAGE("An empty file was detected."),
    INVALID_FILE_EXCEPTION_MESSAGE("Invalid file format."),
    PHOTO_NOT_FOUND_EXCEPTION_MESSAGE("The user's photo was not found matching the specified criteria.");

    private final String exceptionText;

    ExceptionMessage(String exceptionText) {
        this.exceptionText = exceptionText;
    }

    public String getExceptionMessage() {
        return exceptionText;
    }
}
