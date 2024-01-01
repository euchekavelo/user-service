package ru.skillbox.userservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.skillbox.userservice.dto.ShortUserDto;
import ru.skillbox.userservice.dto.UserDto;
import ru.skillbox.userservice.dto.ResponseDto;
import ru.skillbox.userservice.dto.UserSubscriptionDto;
import ru.skillbox.userservice.exception.TownNotFoundException;
import ru.skillbox.userservice.exception.UserNotFoundException;
import ru.skillbox.userservice.exception.UserSubscriptionException;
import ru.skillbox.userservice.model.Town;
import ru.skillbox.userservice.model.User;
import ru.skillbox.userservice.model.UserSubscription;
import ru.skillbox.userservice.model.UserSubscriptionKey;
import ru.skillbox.userservice.model.enums.Sex;
import ru.skillbox.userservice.repository.TownRepository;
import ru.skillbox.userservice.repository.UserRepository;
import ru.skillbox.userservice.repository.UserSubscriptionRepository;
import ru.skillbox.userservice.service.UserService;

import java.util.Optional;
import java.util.UUID;

@Service
 public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TownRepository townRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, TownRepository townRepository,
                           UserSubscriptionRepository userSubscriptionRepository) {

        this.userRepository = userRepository;
        this.townRepository = townRepository;
        this.userSubscriptionRepository = userSubscriptionRepository;
    }

    @Override
    public ResponseDto createUser(ShortUserDto shortUserDto) {
        User user = new User();
        user.setFullname(shortUserDto.getFullname());
        user.setEmail(shortUserDto.getEmail());
        user.setSex(Sex.valueOf(shortUserDto.getSex()));
        userRepository.save(user);

        return getResponseDto("User successfully created.");
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

        return getResponseDto("The user was successfully updated.");
    }

    @Override
    public ResponseDto deleteUserById(UUID id) throws UserNotFoundException {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("The user with the specified ID was not found.");
        }

        userRepository.delete(optionalUser.get());

        return getResponseDto("The user with the specified ID was successfully deleted.");
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

        return getResponseDto("The target user was successfully subscribed.");
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

        return getResponseDto("The destination user has been unsubscribed successfully.");
    }

    private ResponseDto getResponseDto(String message) {
        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage(message);
        responseDto.setResult(true);

        return responseDto;
    }
}
