package ru.skillbox.userservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.skillbox.userservice.dto.ShortUserDto;
import ru.skillbox.userservice.dto.UserDto;
import ru.skillbox.userservice.dto.ResponseDto;
import ru.skillbox.userservice.dto.UserSubscriptionDto;
import ru.skillbox.userservice.exception.*;
import ru.skillbox.userservice.model.*;
import ru.skillbox.userservice.model.enums.Sex;
import ru.skillbox.userservice.repository.*;
import ru.skillbox.userservice.service.UserService;

import java.util.Optional;
import java.util.UUID;

@Service
 public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TownRepository townRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final GroupRepository groupRepository;
    private final UserGroupRepository userGroupRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, TownRepository townRepository,
                           UserSubscriptionRepository userSubscriptionRepository, GroupRepository groupRepository,
                           UserGroupRepository userGroupRepository) {

        this.userRepository = userRepository;
        this.townRepository = townRepository;
        this.userSubscriptionRepository = userSubscriptionRepository;
        this.groupRepository = groupRepository;
        this.userGroupRepository = userGroupRepository;
    }

    @Override
    public ResponseDto createUser(ShortUserDto shortUserDto) {
        User user = new User();
        user.setFullname(shortUserDto.getFullname());
        user.setEmail(shortUserDto.getEmail());
        user.setSex(Sex.valueOf(shortUserDto.getSex()));
        User savedUser = userRepository.save(user);

        return getResponseDto("User successfully created.", savedUser.getId());
    }

    @Override
    public User getUserById(UUID id) throws UserNotFoundException {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("The user with the specified ID was not found.");
        }

        return optionalUser.get();
    }

    @Override
    public ResponseDto updateUserById(UUID id, UserDto userDto) throws UserNotFoundException, TownNotFoundException {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("The user with the specified ID was not found.");
        }

        Optional<Town> optionalTown = townRepository.findById(userDto.getTownId());
        if (optionalTown.isEmpty()) {
            throw new TownNotFoundException("The town with the specified ID was not found.");
        }

        User user = optionalUser.get();
        user.setFullname(userDto.getFullname());
        user.setEmail(userDto.getEmail());
        user.setSex(Sex.valueOf(userDto.getSex()));
        user.setBirthDate(userDto.getBirthDate());
        user.setPhone(userDto.getPhone());
        user.setTown(optionalTown.get());
        userRepository.save(user);

        return getResponseDto("The user was successfully updated.", null);
    }

    @Override
    public ResponseDto deleteUserById(UUID id) throws UserNotFoundException {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("The user with the specified ID was not found.");
        }

        userRepository.delete(optionalUser.get());

        return getResponseDto("The user with the specified ID was successfully deleted.", null);
    }

    @Override
    public ResponseDto subscribeToUser(UserSubscriptionDto userSubscriptionDto) throws UserSubscriptionException,
            UserNotFoundException {

        if (userSubscriptionDto.getSourceUserId().equals(userSubscriptionDto.getDestinationUserId())) {
            throw new UserSubscriptionException("The user cannot subscribe to himself.");
        }

        Optional<User> optionalSourceUser = userRepository.findById(userSubscriptionDto.getSourceUserId());
        if (optionalSourceUser.isEmpty()) {
            throw new UserNotFoundException("The source user with the specified ID was not found.");
        }

        Optional<User> optionalDestinationUser = userRepository.findById(userSubscriptionDto.getDestinationUserId());
        if (optionalDestinationUser.isEmpty()) {
            throw new UserNotFoundException("The destination user with the specified ID was not found.");
        }

        UserSubscriptionKey userSubscriptionKey = new UserSubscriptionKey();
        userSubscriptionKey.setSourceUserId(userSubscriptionDto.getSourceUserId());
        userSubscriptionKey.setDestinationUserId(userSubscriptionDto.getDestinationUserId());

        Optional<UserSubscription> optionalUserSubscription = userSubscriptionRepository.findById(userSubscriptionKey);
        if (optionalUserSubscription.isPresent()) {
            throw new UserSubscriptionException("A subscription to the destination user already exists.");
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
            throw new UserSubscriptionException("The user cannot unsubscribe to himself.");
        }

        UserSubscriptionKey userSubscriptionKey = new UserSubscriptionKey();
        userSubscriptionKey.setSourceUserId(userSubscriptionDto.getSourceUserId());
        userSubscriptionKey.setDestinationUserId(userSubscriptionDto.getDestinationUserId());

        Optional<UserSubscription> optionalUserSubscription = userSubscriptionRepository.findById(userSubscriptionKey);
        if (optionalUserSubscription.isEmpty()) {
            throw new UserSubscriptionException("No subscription has been identified in relation to the destination user.");
        }

        userSubscriptionRepository.delete(optionalUserSubscription.get());

        return getResponseDto("The destination user has been unsubscribed successfully.", null);
    }

    @Override
    public ResponseDto addUserToGroup(UUID userId, UUID groupId) throws UserNotFoundException, GroupNotFoundException {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("The user with the specified ID was not found.");
        }

        Optional<Group> optionalGroup = groupRepository.findById(groupId);
        if (optionalGroup.isEmpty()) {
            throw new GroupNotFoundException("The group with the specified ID was not found.");
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
    public ResponseDto deleteUserFromGroup(UUID userId, UUID groupId) throws UserGroupException {
        UserGroupKey userGroupKey = new UserGroupKey();
        userGroupKey.setUserId(userId);
        userGroupKey.setGroupId(groupId);

        Optional<UserGroup> optionalUserGroup = userGroupRepository.findById(userGroupKey);
        if (optionalUserGroup.isEmpty()) {
            throw new UserGroupException("Cannot remove a user from a group.");
        }

        userGroupRepository.delete(optionalUserGroup.get());

        return getResponseDto("The user has been successfully removed from the group.", null);
    }

    private ResponseDto getResponseDto(String message, UUID id) {
        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage(message);
        responseDto.setResult(true);
        responseDto.setId(id);

        return responseDto;
    }
}
