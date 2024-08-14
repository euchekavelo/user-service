package ru.skillbox.userservice.service;

import ru.skillbox.userservice.dto.ShortUserDto;
import ru.skillbox.userservice.dto.UserDto;
import ru.skillbox.userservice.dto.response.ResponseDto;
import ru.skillbox.userservice.dto.UserSubscriptionDto;
import ru.skillbox.userservice.dto.response.UserResponseDto;
import ru.skillbox.userservice.exception.*;

import java.util.UUID;

public interface UserService {

    UserResponseDto createUser(ShortUserDto shortUserDto);

    UserResponseDto getUserById(UUID id) throws UserNotFoundException;

    UserResponseDto updateUserById(UUID id, UserDto userDto) throws UserNotFoundException, TownNotFoundException;

    void deleteUserById(UUID id) throws UserNotFoundException;

    ResponseDto subscribeToUser(UserSubscriptionDto userSubscriptionDto) throws UserSubscriptionException,
            UserNotFoundException;

    ResponseDto unsubscribeFromUser(UserSubscriptionDto userSubscriptionDto) throws UserSubscriptionException;

    ResponseDto addUserToGroup(UUID userId, UUID groupId) throws UserNotFoundException, GroupNotFoundException;

    ResponseDto deleteUserFromGroup(UUID userId, UUID groupId) throws UserGroupNotFoundException;
}
