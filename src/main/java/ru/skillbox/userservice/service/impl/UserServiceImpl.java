package ru.skillbox.userservice.service.impl;

import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.skillbox.userservice.dto.ShortUserDto;
import ru.skillbox.userservice.dto.UserDto;
import ru.skillbox.userservice.dto.response.ResponseDto;
import ru.skillbox.userservice.dto.UserSubscriptionDto;
import ru.skillbox.userservice.dto.response.UserResponseDto;
import ru.skillbox.userservice.exception.*;
import ru.skillbox.userservice.mapper.UserMapper;
import ru.skillbox.userservice.model.*;
import ru.skillbox.userservice.model.enums.Sex;
import ru.skillbox.userservice.repository.*;
import ru.skillbox.userservice.service.UserService;

import java.util.Optional;
import java.util.UUID;

import static ru.skillbox.userservice.exception.enums.ExceptionMessage.*;

@Observed
@Service
 public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TownRepository townRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final GroupRepository groupRepository;
    private final UserGroupRepository userGroupRepository;
    private final UserMapper userMapper;
    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    public UserServiceImpl(UserRepository userRepository, TownRepository townRepository,
                           UserSubscriptionRepository userSubscriptionRepository, GroupRepository groupRepository,
                           UserGroupRepository userGroupRepository, UserMapper userMapper) {

        this.userRepository = userRepository;
        this.townRepository = townRepository;
        this.userSubscriptionRepository = userSubscriptionRepository;
        this.groupRepository = groupRepository;
        this.userGroupRepository = userGroupRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserResponseDto createUser(ShortUserDto shortUserDto) {
        User user = new User();
        user.setFullname(shortUserDto.getFullname());
        user.setEmail(shortUserDto.getEmail());
        user.setSex(Sex.valueOf(shortUserDto.getSex()));
        User savedUser = userRepository.save(user);

        return userMapper.userToUserResponseDto(savedUser);
    }

    @Override
    public UserResponseDto getUserById(UUID id) throws UserNotFoundException {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            logger.error(USER_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
            throw new UserNotFoundException(USER_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
        }

        return userMapper.userToUserResponseDto(optionalUser.get());
    }

    @Override
    public UserResponseDto updateUserById(UUID id, UserDto userDto) throws UserNotFoundException, TownNotFoundException {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            logger.error(USER_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
            throw new UserNotFoundException(USER_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
        }

        Optional<Town> optionalTown = townRepository.findById(userDto.getTownId());
        if (optionalTown.isEmpty()) {
            logger.error(TOWN_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
            throw new TownNotFoundException(TOWN_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
        }

        User user = optionalUser.get();
        user.setFullname(userDto.getFullname());
        user.setEmail(userDto.getEmail());
        user.setSex(Sex.valueOf(userDto.getSex()));
        user.setBirthDate(userDto.getBirthDate());
        user.setPhone(userDto.getPhone());
        user.setTown(optionalTown.get());
        User updatedUser = userRepository.save(user);

        return userMapper.userToUserResponseDto(updatedUser);
    }

    @Override
    public void deleteUserById(UUID id) throws UserNotFoundException {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            logger.error(USER_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
            throw new UserNotFoundException(USER_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
        }

        userRepository.delete(optionalUser.get());
    }

    @Override
    public ResponseDto subscribeToUser(UserSubscriptionDto userSubscriptionDto) throws UserSubscriptionException,
            UserNotFoundException {

        if (userSubscriptionDto.getSourceUserId().equals(userSubscriptionDto.getDestinationUserId())) {
            logger.error(USER_SUBSCRIPTION_EXCEPTION_MESSAGE.getExceptionMessage());
            throw new UserSubscriptionException(USER_SUBSCRIPTION_EXCEPTION_MESSAGE.getExceptionMessage());
        }

        Optional<User> optionalSourceUser = userRepository.findById(userSubscriptionDto.getSourceUserId());
        if (optionalSourceUser.isEmpty()) {
            logger.error(SOURCE_USER_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
            throw new UserNotFoundException(SOURCE_USER_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
        }

        Optional<User> optionalDestinationUser = userRepository.findById(userSubscriptionDto.getDestinationUserId());
        if (optionalDestinationUser.isEmpty()) {
            logger.error(DESTINATION_USER_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
            throw new UserNotFoundException(DESTINATION_USER_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
        }

        UserSubscriptionKey userSubscriptionKey = new UserSubscriptionKey();
        userSubscriptionKey.setSourceUserId(userSubscriptionDto.getSourceUserId());
        userSubscriptionKey.setDestinationUserId(userSubscriptionDto.getDestinationUserId());

        Optional<UserSubscription> optionalUserSubscription = userSubscriptionRepository.findById(userSubscriptionKey);
        if (optionalUserSubscription.isPresent()) {
            logger.error(USER_SUBSCRIPTION_ALREADY_EXISTS_EXCEPTION_MESSAGE.getExceptionMessage());
            throw new UserSubscriptionException(USER_SUBSCRIPTION_ALREADY_EXISTS_EXCEPTION_MESSAGE.getExceptionMessage());
        }

        UserSubscription userSubscription = new UserSubscription();
        userSubscription.setUserSubscriptionKey(userSubscriptionKey);
        userSubscription.setSourceUser(optionalSourceUser.get());
        userSubscription.setDestinationUser(optionalDestinationUser.get());
        userSubscriptionRepository.save(userSubscription);

        return getResponseDto("The target user was successfully subscribed.", null);
    }

    @Override
    public ResponseDto unsubscribeFromUser(UserSubscriptionDto userSubscriptionDto) throws UserSubscriptionException {
        if (userSubscriptionDto.getSourceUserId().equals(userSubscriptionDto.getDestinationUserId())) {
            logger.error(USER_UNSUBSCRIBE_HIMSELF_EXCEPTION_MESSAGE.getExceptionMessage());
            throw new UserSubscriptionException(USER_UNSUBSCRIBE_HIMSELF_EXCEPTION_MESSAGE.getExceptionMessage());
        }

        UserSubscriptionKey userSubscriptionKey = new UserSubscriptionKey();
        userSubscriptionKey.setSourceUserId(userSubscriptionDto.getSourceUserId());
        userSubscriptionKey.setDestinationUserId(userSubscriptionDto.getDestinationUserId());

        Optional<UserSubscription> optionalUserSubscription = userSubscriptionRepository.findById(userSubscriptionKey);
        if (optionalUserSubscription.isEmpty()) {
            logger.error(USER_SUBSCRIPTION_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
            throw new UserSubscriptionException(USER_SUBSCRIPTION_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
        }

        userSubscriptionRepository.delete(optionalUserSubscription.get());

        return getResponseDto("The destination user has been unsubscribed successfully.", null);
    }

    @Override
    public ResponseDto addUserToGroup(UUID userId, UUID groupId) throws UserNotFoundException, GroupNotFoundException {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            logger.error(USER_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
            throw new UserNotFoundException(USER_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
        }

        Optional<Group> optionalGroup = groupRepository.findById(groupId);
        if (optionalGroup.isEmpty()) {
            logger.error(GROUP_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
            throw new GroupNotFoundException(GROUP_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
        }

        UserGroupKey userGroupKey = new UserGroupKey();
        userGroupKey.setUserId(userId);
        userGroupKey.setGroupId(groupId);

        UserGroup userGroup = new UserGroup();
        userGroup.setUserGroupKey(userGroupKey);
        userGroup.setUser(optionalUser.get());
        userGroup.setGroup(optionalGroup.get());
        userGroupRepository.save(userGroup);

        return getResponseDto("The user has been successfully added to the group.", null);
    }

    @Override
    public ResponseDto deleteUserFromGroup(UUID userId, UUID groupId) throws UserGroupNotFoundException {
        UserGroupKey userGroupKey = new UserGroupKey();
        userGroupKey.setUserId(userId);
        userGroupKey.setGroupId(groupId);

        Optional<UserGroup> optionalUserGroup = userGroupRepository.findById(userGroupKey);
        if (optionalUserGroup.isEmpty()) {
            logger.error(IMPOSSIBLE_REMOVING_USER_GROUP_EXCEPTION_MESSAGE.getExceptionMessage());
            throw new UserGroupNotFoundException(IMPOSSIBLE_REMOVING_USER_GROUP_EXCEPTION_MESSAGE.getExceptionMessage());
        }

        userGroupRepository.delete(optionalUserGroup.get());

        return getResponseDto("The user has been successfully removed from the group.", null);
    }

    private ResponseDto getResponseDto(String message, UUID id) {
        return ResponseDto.builder()
                .message(message)
                .id(id)
                .result(true)
                .build();
    }
}
