package ru.skillbox.userservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.skillbox.userservice.config.ConfigUserServiceTest;
import ru.skillbox.userservice.dto.ResponseDto;
import ru.skillbox.userservice.dto.ShortUserDto;
import ru.skillbox.userservice.dto.UserDto;
import ru.skillbox.userservice.dto.UserSubscriptionDto;
import ru.skillbox.userservice.exception.*;
import ru.skillbox.userservice.model.*;
import ru.skillbox.userservice.model.enums.Sex;
import ru.skillbox.userservice.repository.*;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ConfigUserServiceTest.class)
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TownRepository townRepository;

    @Autowired
    private UserSubscriptionRepository userSubscriptionRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Test
    void createUserTestSuccess() {
        User savedUser = new User();
        savedUser.setId(UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd"));
        savedUser.setFullname("Ivanov Ivan Ivanovich");
        savedUser.setEmail("invanov_test@gmail.com");
        savedUser.setSex(Sex.valueOf("MALE"));

        Mockito.when(userRepository.save(Mockito.any())).thenReturn(savedUser);
        ShortUserDto shortUserDto = new ShortUserDto();
        shortUserDto.setFullname("Ivanov Ivan Ivanovich");
        shortUserDto.setEmail("invanov_test@gmail.com");
        shortUserDto.setSex("MALE");
        ResponseDto responseDto = userService.createUser(shortUserDto);

        assertThat(responseDto.getMessage()).isEqualTo("User successfully created.");
    }

    @Test
    void getUserByIdTestSuccess() throws UserNotFoundException {
        UUID uuid = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");
        User user = new User();
        user.setId(uuid);
        user.setEmail("invanov_test@gmail.com");
        user.setFullname("Ivanov Ivan Ivanovich");
        user.setSex(Sex.MALE);
        Optional<User> optionalUser = Optional.of(user);
        Mockito.when(userRepository.findById(Mockito.any(UUID.class))).thenReturn(optionalUser);
        User savedUser = userService.getUserById(uuid);

        assertThat(savedUser.getEmail()).isEqualTo("invanov_test@gmail.com");
    }

    @Test
    void getUserByIdThrowUserNotFoundException() {
        UUID uuid = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");
        Mockito.when(userRepository.findById(uuid)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(uuid));
    }

    @Test
    void updateUserByIdTestSuccess() throws UserNotFoundException, TownNotFoundException {
        UUID userId = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");
        UUID townId = UUID.fromString("11cfa0c0-2fe3-47d9-916b-761e59b67ccf");

        UserDto userDto = new UserDto();
        userDto.setFullname("Ivanov Ivan Ivanovich");
        userDto.setEmail("invanov_test@gmail.com");
        userDto.setSex("MALE");
        userDto.setBirthDate(LocalDate.parse("2000-01-01"));
        userDto.setPhone("+79999999999");
        userDto.setTownId(townId);

        User user = new User();
        user.setId(userId);
        user.setEmail("petrov_test@gmail.com");
        user.setFullname("Petrov Ivan Ivanovich");
        user.setSex(Sex.MALE);
        Optional<User> optionalUser = Optional.of(user);

        Town town = new Town();
        town.setName("Moscow");
        Optional<Town> optionalTown = Optional.of(town);
        Mockito.when(userRepository.findById(userId)).thenReturn(optionalUser);
        Mockito.when(townRepository.findById(townId)).thenReturn(optionalTown);

        assertThat(userService.updateUserById(userId, userDto).getMessage())
                .isEqualTo("The user was successfully updated.");
    }

    @Test
    void updateUserByIdTestThrowUserNotFoundException() {
        UUID userId = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");
        UUID townId = UUID.fromString("11cfa0c0-2fe3-47d9-916b-761e59b67ccf");

        UserDto userDto = new UserDto();
        userDto.setFullname("Ivanov Ivan Ivanovich");
        userDto.setEmail("invanov_test@gmail.com");
        userDto.setSex("MALE");
        userDto.setBirthDate(LocalDate.parse("2000-01-01"));
        userDto.setPhone("+79999999999");
        userDto.setTownId(townId);
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUserById(userId, userDto));
    }

    @Test
    void updateUserByIdTestThrowTownNotFoundException() {
        UUID userId = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");
        UUID townId = UUID.fromString("11cfa0c0-2fe3-47d9-916b-761e59b67ccf");

        UserDto userDto = new UserDto();
        userDto.setFullname("Ivanov Ivan Ivanovich");
        userDto.setEmail("invanov_test@gmail.com");
        userDto.setSex("MALE");
        userDto.setBirthDate(LocalDate.parse("2000-01-01"));
        userDto.setPhone("+79999999999");
        userDto.setTownId(townId);

        User user = new User();
        user.setId(userId);
        user.setEmail("petrov_test@gmail.com");
        user.setFullname("Petrov Ivan Ivanovich");
        user.setSex(Sex.MALE);
        Optional<User> optionalUser = Optional.of(user);
        Mockito.when(userRepository.findById(userId)).thenReturn(optionalUser);
        Mockito.when(townRepository.findById(townId)).thenReturn(Optional.empty());

        assertThrows(TownNotFoundException.class, () -> userService.updateUserById(userId, userDto));
    }

    @Test
    void deleteUserByIdTestSuccess() throws UserNotFoundException {
        UUID userId = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");
        User user = new User();
        user.setId(userId);
        user.setEmail("petrov_test@gmail.com");
        user.setFullname("Petrov Ivan Ivanovich");
        user.setSex(Sex.MALE);
        Optional<User> optionalUser = Optional.of(user);
        Mockito.when(userRepository.findById(userId)).thenReturn(optionalUser);

        assertThat(userService.deleteUserById(userId).getMessage())
                .isEqualTo("The user with the specified ID was successfully deleted.");
    }

    @Test
    void deleteUserByIdTestThrowUserNotFoundException() {
        UUID userId = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUserById(userId));
    }

    @Test
    void subscribeToUserTestSuccess() throws UserNotFoundException, UserSubscriptionException {
        UUID sourceUserId = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");
        UUID destinationUserId = UUID.fromString("15afa0c0-2fe3-47d9-916b-761e59b67caa");

        UserSubscriptionDto userSubscriptionDto = new UserSubscriptionDto();
        userSubscriptionDto.setSourceUserId(sourceUserId);
        userSubscriptionDto.setDestinationUserId(destinationUserId);

        User sourceUser = new User();
        sourceUser.setId(sourceUserId);
        sourceUser.setEmail("petrov_test@gmail.com");
        sourceUser.setFullname("Petrov Ivan Ivanovich");
        sourceUser.setSex(Sex.MALE);

        User destinationUser = new User();
        destinationUser.setId(sourceUserId);
        destinationUser.setEmail("ivanov_test@gmail.com");
        destinationUser.setFullname("Ivanov Ivan Ivanovich");
        destinationUser.setSex(Sex.MALE);

        Mockito.when(userRepository.findById(sourceUserId)).thenReturn(Optional.of(sourceUser));
        Mockito.when(userRepository.findById(destinationUserId)).thenReturn(Optional.of(destinationUser));

        UserSubscriptionKey userSubscriptionKey = new UserSubscriptionKey();
        userSubscriptionKey.setSourceUserId(userSubscriptionDto.getSourceUserId());
        userSubscriptionKey.setDestinationUserId(userSubscriptionDto.getDestinationUserId());
        Mockito.when(userSubscriptionRepository.findById(userSubscriptionKey)).thenReturn(Optional.empty());

        assertThat(userService.subscribeToUser(userSubscriptionDto).getMessage())
                .isEqualTo("The target user was successfully subscribed.");
    }

    @Test
    void subscribeToUserTestThrowUserSubscriptionExceptionWhenSubscribeToYourself() {
        UUID sourceUserId = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");
        UUID destinationUserId = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");

        UserSubscriptionDto userSubscriptionDto = new UserSubscriptionDto();
        userSubscriptionDto.setSourceUserId(sourceUserId);
        userSubscriptionDto.setDestinationUserId(destinationUserId);

        assertThrows(UserSubscriptionException.class, () -> userService.subscribeToUser(userSubscriptionDto));
    }

    @Test
    void subscribeToUserTestThrowUserNotFoundExceptionWhenSourceUserNotFound() {
        UUID sourceUserId = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");
        UUID destinationUserId = UUID.fromString("15afa0c0-2fe3-47d9-916b-761e59b67caa");

        UserSubscriptionDto userSubscriptionDto = new UserSubscriptionDto();
        userSubscriptionDto.setSourceUserId(sourceUserId);
        userSubscriptionDto.setDestinationUserId(destinationUserId);

        User destinationUser = new User();
        destinationUser.setId(sourceUserId);
        destinationUser.setEmail("ivanov_test@gmail.com");
        destinationUser.setFullname("Ivanov Ivan Ivanovich");
        destinationUser.setSex(Sex.MALE);
        Mockito.when(userRepository.findById(sourceUserId)).thenReturn(Optional.empty());
        Mockito.when(userRepository.findById(destinationUserId)).thenReturn(Optional.of(destinationUser));

        assertThrows(UserNotFoundException.class, () -> userService.subscribeToUser(userSubscriptionDto));
    }

    @Test
    void subscribeToUserTestThrowUserNotFoundExceptionWhenDestinationUserNotFound() {
        UUID sourceUserId = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");
        UUID destinationUserId = UUID.fromString("15afa0c0-2fe3-47d9-916b-761e59b67caa");

        UserSubscriptionDto userSubscriptionDto = new UserSubscriptionDto();
        userSubscriptionDto.setSourceUserId(sourceUserId);
        userSubscriptionDto.setDestinationUserId(destinationUserId);

        User sourceUser = new User();
        sourceUser.setId(sourceUserId);
        sourceUser.setEmail("petrov_test@gmail.com");
        sourceUser.setFullname("Petrov Ivan Ivanovich");
        sourceUser.setSex(Sex.MALE);
        Mockito.when(userRepository.findById(sourceUserId)).thenReturn(Optional.of(sourceUser));
        Mockito.when(userRepository.findById(destinationUserId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.subscribeToUser(userSubscriptionDto));
    }

    @Test
    void subscribeToUserTestThrowUserSubscriptionExceptionWhenExists() {
        UUID sourceUserId = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");
        UUID destinationUserId = UUID.fromString("15afa0c0-2fe3-47d9-916b-761e59b67caa");

        UserSubscriptionDto userSubscriptionDto = new UserSubscriptionDto();
        userSubscriptionDto.setSourceUserId(sourceUserId);
        userSubscriptionDto.setDestinationUserId(destinationUserId);

        User sourceUser = new User();
        sourceUser.setId(sourceUserId);
        sourceUser.setEmail("petrov_test@gmail.com");
        sourceUser.setFullname("Petrov Ivan Ivanovich");
        sourceUser.setSex(Sex.MALE);

        User destinationUser = new User();
        destinationUser.setId(sourceUserId);
        destinationUser.setEmail("ivanov_test@gmail.com");
        destinationUser.setFullname("Ivanov Ivan Ivanovich");
        destinationUser.setSex(Sex.MALE);

        Mockito.when(userRepository.findById(sourceUserId)).thenReturn(Optional.of(sourceUser));
        Mockito.when(userRepository.findById(destinationUserId)).thenReturn(Optional.of(destinationUser));

        UserSubscriptionKey userSubscriptionKey = new UserSubscriptionKey();
        userSubscriptionKey.setSourceUserId(userSubscriptionDto.getSourceUserId());
        userSubscriptionKey.setDestinationUserId(userSubscriptionDto.getDestinationUserId());
        Mockito.when(userSubscriptionRepository.findById(userSubscriptionKey))
                .thenReturn(Optional.of(Mockito.mock(UserSubscription.class)));

        assertThrows(UserSubscriptionException.class, () -> userService.subscribeToUser(userSubscriptionDto));
    }

    @Test
    void unsubscribeFromUserTestSuccess() throws UserSubscriptionException {
        UUID sourceUserId = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");
        UUID destinationUserId = UUID.fromString("15afa0c0-2fe3-47d9-916b-761e59b67caa");

        UserSubscriptionDto userSubscriptionDto = new UserSubscriptionDto();
        userSubscriptionDto.setSourceUserId(sourceUserId);
        userSubscriptionDto.setDestinationUserId(destinationUserId);

        UserSubscriptionKey userSubscriptionKey = new UserSubscriptionKey();
        userSubscriptionKey.setSourceUserId(userSubscriptionDto.getSourceUserId());
        userSubscriptionKey.setDestinationUserId(userSubscriptionDto.getDestinationUserId());
        Mockito.when(userSubscriptionRepository.findById(userSubscriptionKey))
                .thenReturn(Optional.of(Mockito.mock(UserSubscription.class)));

        assertThat(userService.unsubscribeFromUser(userSubscriptionDto).getMessage())
                .isEqualTo("The destination user has been unsubscribed successfully.");
    }

    @Test
    void unsubscribeFromUserTestThrowUserSubscriptionExceptionWhenUserUnsubscribeYourself() {
        UUID sourceUserId = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");
        UUID destinationUserId = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");

        UserSubscriptionDto userSubscriptionDto = new UserSubscriptionDto();
        userSubscriptionDto.setSourceUserId(sourceUserId);
        userSubscriptionDto.setDestinationUserId(destinationUserId);

        assertThrows(UserSubscriptionException.class, () -> userService.unsubscribeFromUser(userSubscriptionDto));
    }

    @Test
    void unsubscribeFromUserTestThrowUserSubscriptionExceptionWhenSubscriptionNotExists() {
        UUID sourceUserId = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");
        UUID destinationUserId = UUID.fromString("15afa0c0-2fe3-47d9-916b-761e59b67caa");

        UserSubscriptionDto userSubscriptionDto = new UserSubscriptionDto();
        userSubscriptionDto.setSourceUserId(sourceUserId);
        userSubscriptionDto.setDestinationUserId(destinationUserId);

        UserSubscriptionKey userSubscriptionKey = new UserSubscriptionKey();
        userSubscriptionKey.setSourceUserId(userSubscriptionDto.getSourceUserId());
        userSubscriptionKey.setDestinationUserId(userSubscriptionDto.getDestinationUserId());
        Mockito.when(userSubscriptionRepository.findById(userSubscriptionKey)).thenReturn(Optional.empty());

        assertThrows(UserSubscriptionException.class, () -> userService.unsubscribeFromUser(userSubscriptionDto));
    }

    @Test
    void addUserToGroupTestSuccess() throws UserNotFoundException, GroupNotFoundException {
        UUID userId = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");
        UUID groupId = UUID.fromString("15afa0c0-2fe3-47d9-916b-761e59b67caa");
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(Mockito.mock(User.class)));
        Mockito.when(groupRepository.findById(groupId)).thenReturn(Optional.of(Mockito.mock(Group.class)));

        assertThat(userService.addUserToGroup(userId, groupId).getMessage())
                .isEqualTo("The user has been successfully added to the group.");
    }

    @Test
    void addUserToGroupTestThrowUserNotFoundException() {
        UUID userId = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");
        UUID groupId = UUID.fromString("15afa0c0-2fe3-47d9-916b-761e59b67caa");
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());
        Mockito.when(groupRepository.findById(groupId)).thenReturn(Optional.of(Mockito.mock(Group.class)));

        assertThrows(UserNotFoundException.class, () -> userService.addUserToGroup(userId, groupId));
    }

    @Test
    void addUserToGroupTestThrowGroupNotFoundException() {
        UUID userId = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");
        UUID groupId = UUID.fromString("15afa0c0-2fe3-47d9-916b-761e59b67caa");
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(Mockito.mock(User.class)));
        Mockito.when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

        assertThrows(GroupNotFoundException.class, () -> userService.addUserToGroup(userId, groupId));
    }

    @Test
    void deleteUserFromGroupTestSuccess() throws UserGroupException {
        UUID userId = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");
        UUID groupId = UUID.fromString("15afa0c0-2fe3-47d9-916b-761e59b67caa");

        UserGroupKey userGroupKey = new UserGroupKey();
        userGroupKey.setUserId(userId);
        userGroupKey.setGroupId(groupId);
        Mockito.when(userGroupRepository.findById(userGroupKey)).thenReturn(Optional.of(Mockito.mock(UserGroup.class)));

        assertThat(userService.deleteUserFromGroup(userId, groupId).getMessage())
                .isEqualTo("The user has been successfully removed from the group.");
    }

    @Test
    void deleteUserFromGroupTestThrowUserGroupException() {
        UUID userId = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");
        UUID groupId = UUID.fromString("15afa0c0-2fe3-47d9-916b-761e59b67caa");

        UserGroupKey userGroupKey = new UserGroupKey();
        userGroupKey.setUserId(userId);
        userGroupKey.setGroupId(groupId);
        Mockito.when(userGroupRepository.findById(userGroupKey)).thenReturn(Optional.empty());

        assertThrows(UserGroupException.class, () -> userService.deleteUserFromGroup(userId, groupId));
    }
}