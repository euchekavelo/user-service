package ru.tw1.euchekavelo.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.tw1.euchekavelo.config.ConfigUserService;
import ru.tw1.euchekavelo.dto.request.UserRequestDto;
import ru.tw1.euchekavelo.dto.request.UserSubscriptionDto;
import ru.tw1.euchekavelo.dto.response.UserResponseDto;
import ru.skillbox.userservice.exception.*;
import ru.skillbox.userservice.model.*;
import ru.tw1.euchekavelo.exception.*;
import ru.tw1.euchekavelo.model.*;
import ru.tw1.euchekavelo.model.enums.Sex;
import ru.skillbox.userservice.repository.*;
import ru.tw1.euchekavelo.repository.*;
import ru.tw1.euchekavelo.service.application.UserApplicationService;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ConfigUserService.class)
class UserApplicationServiceTest {

    @Autowired
    private UserApplicationService userApplicationService;

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

    /*@Test
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
        UserResponseDto userResponseDto = userService.createUser(shortUserDto);

        assertThat(userResponseDto.getId()).isNotNull();
    }*/

    @Test
    void getUserByIdTestSuccess() throws UserNotFoundException {
        UUID uuid = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");
        User user = new User();
        user.setId(uuid);
        user.setEmail("invanov_test@gmail.com");
        user.setFullName("Ivanov Ivan Ivanovich");
        user.setSex(Sex.MALE);
        Optional<User> optionalUser = Optional.of(user);
        Mockito.when(userRepository.findById(Mockito.any(UUID.class))).thenReturn(optionalUser);

        UserResponseDto userResponseDto = userApplicationService.getUserById(uuid);
        assertThat(userResponseDto.getEmail()).isEqualTo("invanov_test@gmail.com");
    }

    @Test
    void getUserByIdThrowUserNotFoundException() {
        UUID uuid = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");
        Mockito.when(userRepository.findById(uuid)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userApplicationService.getUserById(uuid));
    }

    @Test
    void updateUserByIdTestSuccess() throws UserNotFoundException, TownNotFoundException {
        UUID userId = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");
        UUID townId = UUID.fromString("11cfa0c0-2fe3-47d9-916b-761e59b67ccf");

        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setFullname("Ivanov Ivan Ivanovich");
        userRequestDto.setEmail("invanov_test@gmail.com");
        userRequestDto.setSex("MALE");
        userRequestDto.setBirthDate(LocalDate.parse("2000-01-01"));
        userRequestDto.setPhone("+79999999999");
        userRequestDto.setTownId(townId);

        User user = new User();
        user.setId(userId);
        user.setEmail("petrov_test@gmail.com");
        user.setFullName("Petrov Ivan Ivanovich");
        user.setSex(Sex.MALE);
        Optional<User> optionalUser = Optional.of(user);

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setEmail(userRequestDto.getEmail());
        updatedUser.setFullName(userRequestDto.getFullname());
        updatedUser.setSex(Sex.valueOf(userRequestDto.getSex()));
        updatedUser.setBirthDate(userRequestDto.getBirthDate());
        updatedUser.setPhone(userRequestDto.getPhone());

        Town town = new Town();
        town.setName("Moscow");
        Optional<Town> optionalTown = Optional.of(town);

        Mockito.when(userRepository.findById(userId)).thenReturn(optionalUser);
        Mockito.when(townRepository.findById(townId)).thenReturn(optionalTown);
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(updatedUser);

        assertThat(userApplicationService.updateUserById(userId, userRequestDto).getEmail())
                .isEqualTo("invanov_test@gmail.com");
    }

    @Test
    void updateUserByIdTestThrowUserNotFoundException() {
        UUID userId = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");
        UUID townId = UUID.fromString("11cfa0c0-2fe3-47d9-916b-761e59b67ccf");

        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setFullname("Ivanov Ivan Ivanovich");
        userRequestDto.setEmail("invanov_test@gmail.com");
        userRequestDto.setSex("MALE");
        userRequestDto.setBirthDate(LocalDate.parse("2000-01-01"));
        userRequestDto.setPhone("+79999999999");
        userRequestDto.setTownId(townId);
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userApplicationService.updateUserById(userId, userRequestDto));
    }

    @Test
    void updateUserByIdTestThrowTownNotFoundException() {
        UUID userId = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");
        UUID townId = UUID.fromString("11cfa0c0-2fe3-47d9-916b-761e59b67ccf");

        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setFullname("Ivanov Ivan Ivanovich");
        userRequestDto.setEmail("invanov_test@gmail.com");
        userRequestDto.setSex("MALE");
        userRequestDto.setBirthDate(LocalDate.parse("2000-01-01"));
        userRequestDto.setPhone("+79999999999");
        userRequestDto.setTownId(townId);

        User user = new User();
        user.setId(userId);
        user.setEmail("petrov_test@gmail.com");
        user.setFullName("Petrov Ivan Ivanovich");
        user.setSex(Sex.MALE);
        Optional<User> optionalUser = Optional.of(user);
        Mockito.when(userRepository.findById(userId)).thenReturn(optionalUser);
        Mockito.when(townRepository.findById(townId)).thenReturn(Optional.empty());

        assertThrows(TownNotFoundException.class, () -> userApplicationService.updateUserById(userId, userRequestDto));
    }

    /*@Test
    void deleteUserByIdTestSuccess() throws UserNotFoundException {
        UUID userId = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");
        User user = new User();
        user.setId(userId);
        user.setEmail("petrov_test@gmail.com");
        user.setFullname("Petrov Ivan Ivanovich");
        user.setSex(Sex.MALE);
        Optional<User> optionalUser = Optional.of(user);
        Mockito.when(userRepository.findById(userId)).thenReturn(optionalUser);

        assertDoesNotThrow(() -> userService.deleteUserById(userId));
    }

    @Test
    void deleteUserByIdTestThrowUserNotFoundException() {
        UUID userId = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUserById(userId));
    }*/

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
        sourceUser.setFullName("Petrov Ivan Ivanovich");
        sourceUser.setSex(Sex.MALE);

        User destinationUser = new User();
        destinationUser.setId(sourceUserId);
        destinationUser.setEmail("ivanov_test@gmail.com");
        destinationUser.setFullName("Ivanov Ivan Ivanovich");
        destinationUser.setSex(Sex.MALE);

        Mockito.when(userRepository.findById(sourceUserId)).thenReturn(Optional.of(sourceUser));
        Mockito.when(userRepository.findById(destinationUserId)).thenReturn(Optional.of(destinationUser));

        UserSubscriptionKey userSubscriptionKey = new UserSubscriptionKey();
        userSubscriptionKey.setSourceUserId(userSubscriptionDto.getSourceUserId());
        userSubscriptionKey.setDestinationUserId(userSubscriptionDto.getDestinationUserId());
        Mockito.when(userSubscriptionRepository.findById(userSubscriptionKey)).thenReturn(Optional.empty());

        assertThat(userApplicationService.subscribeToUser(userSubscriptionDto).getMessage())
                .isEqualTo("The target user was successfully subscribed.");
    }

    @Test
    void subscribeToUserTestThrowUserSubscriptionExceptionWhenSubscribeToYourself() {
        UUID sourceUserId = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");
        UUID destinationUserId = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");

        UserSubscriptionDto userSubscriptionDto = new UserSubscriptionDto();
        userSubscriptionDto.setSourceUserId(sourceUserId);
        userSubscriptionDto.setDestinationUserId(destinationUserId);

        assertThrows(UserSubscriptionException.class, () -> userApplicationService.subscribeToUser(userSubscriptionDto));
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
        destinationUser.setFullName("Ivanov Ivan Ivanovich");
        destinationUser.setSex(Sex.MALE);
        Mockito.when(userRepository.findById(sourceUserId)).thenReturn(Optional.empty());
        Mockito.when(userRepository.findById(destinationUserId)).thenReturn(Optional.of(destinationUser));

        assertThrows(UserNotFoundException.class, () -> userApplicationService.subscribeToUser(userSubscriptionDto));
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
        sourceUser.setFullName("Petrov Ivan Ivanovich");
        sourceUser.setSex(Sex.MALE);
        Mockito.when(userRepository.findById(sourceUserId)).thenReturn(Optional.of(sourceUser));
        Mockito.when(userRepository.findById(destinationUserId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userApplicationService.subscribeToUser(userSubscriptionDto));
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
        sourceUser.setFullName("Petrov Ivan Ivanovich");
        sourceUser.setSex(Sex.MALE);

        User destinationUser = new User();
        destinationUser.setId(sourceUserId);
        destinationUser.setEmail("ivanov_test@gmail.com");
        destinationUser.setFullName("Ivanov Ivan Ivanovich");
        destinationUser.setSex(Sex.MALE);

        Mockito.when(userRepository.findById(sourceUserId)).thenReturn(Optional.of(sourceUser));
        Mockito.when(userRepository.findById(destinationUserId)).thenReturn(Optional.of(destinationUser));

        UserSubscriptionKey userSubscriptionKey = new UserSubscriptionKey();
        userSubscriptionKey.setSourceUserId(userSubscriptionDto.getSourceUserId());
        userSubscriptionKey.setDestinationUserId(userSubscriptionDto.getDestinationUserId());
        Mockito.when(userSubscriptionRepository.findById(userSubscriptionKey))
                .thenReturn(Optional.of(Mockito.mock(UserSubscription.class)));

        assertThrows(UserSubscriptionException.class, () -> userApplicationService.subscribeToUser(userSubscriptionDto));
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

        assertThat(userApplicationService.unsubscribeFromUser(userSubscriptionDto).getMessage())
                .isEqualTo("The destination user has been unsubscribed successfully.");
    }

    @Test
    void unsubscribeFromUserTestThrowUserSubscriptionExceptionWhenUserUnsubscribeYourself() {
        UUID sourceUserId = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");
        UUID destinationUserId = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");

        UserSubscriptionDto userSubscriptionDto = new UserSubscriptionDto();
        userSubscriptionDto.setSourceUserId(sourceUserId);
        userSubscriptionDto.setDestinationUserId(destinationUserId);

        assertThrows(UserSubscriptionException.class, () -> userApplicationService.unsubscribeFromUser(userSubscriptionDto));
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

        assertThrows(UserSubscriptionException.class, () -> userApplicationService.unsubscribeFromUser(userSubscriptionDto));
    }

    @Test
    void addUserToGroupTestSuccess() throws UserNotFoundException, GroupNotFoundException {
        UUID userId = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");
        UUID groupId = UUID.fromString("15afa0c0-2fe3-47d9-916b-761e59b67caa");
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(Mockito.mock(User.class)));
        Mockito.when(groupRepository.findById(groupId)).thenReturn(Optional.of(Mockito.mock(Group.class)));

        assertThat(userApplicationService.addUserToGroup(userId, groupId).getMessage())
                .isEqualTo("The user has been successfully added to the group.");
    }

    @Test
    void addUserToGroupTestThrowUserNotFoundException() {
        UUID userId = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");
        UUID groupId = UUID.fromString("15afa0c0-2fe3-47d9-916b-761e59b67caa");
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());
        Mockito.when(groupRepository.findById(groupId)).thenReturn(Optional.of(Mockito.mock(Group.class)));

        assertThrows(UserNotFoundException.class, () -> userApplicationService.addUserToGroup(userId, groupId));
    }

    @Test
    void addUserToGroupTestThrowGroupNotFoundException() {
        UUID userId = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");
        UUID groupId = UUID.fromString("15afa0c0-2fe3-47d9-916b-761e59b67caa");
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(Mockito.mock(User.class)));
        Mockito.when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

        assertThrows(GroupNotFoundException.class, () -> userApplicationService.addUserToGroup(userId, groupId));
    }

    @Test
    void deleteUserFromGroupTestSuccess() throws UserGroupNotFoundException {
        UUID userId = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");
        UUID groupId = UUID.fromString("15afa0c0-2fe3-47d9-916b-761e59b67caa");

        UserGroupKey userGroupKey = new UserGroupKey();
        userGroupKey.setUserId(userId);
        userGroupKey.setGroupId(groupId);
        Mockito.when(userGroupRepository.findById(userGroupKey)).thenReturn(Optional.of(Mockito.mock(UserGroup.class)));

        assertThat(userApplicationService.deleteUserFromGroup(userId, groupId).getMessage())
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

        assertThrows(UserGroupNotFoundException.class, () -> userApplicationService.deleteUserFromGroup(userId, groupId));
    }
}