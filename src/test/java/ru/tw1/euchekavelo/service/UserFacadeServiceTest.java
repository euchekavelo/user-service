package ru.tw1.euchekavelo.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.tw1.euchekavelo.client.api.AuthServiceApiClient;
import ru.tw1.euchekavelo.config.ConfigUserApplicationService;
import ru.tw1.euchekavelo.dto.external.response.TokenResponseDto;
import ru.tw1.euchekavelo.dto.external.response.UserExternalResponseDto;
import ru.tw1.euchekavelo.dto.request.ShortUserRequestDto;
import ru.tw1.euchekavelo.dto.request.UserRequestDto;
import ru.tw1.euchekavelo.dto.request.UserSubscriptionDto;
import ru.tw1.euchekavelo.dto.response.UserResponseDto;
import ru.tw1.euchekavelo.exception.*;
import ru.tw1.euchekavelo.model.*;
import ru.tw1.euchekavelo.model.enums.Sex;
import ru.tw1.euchekavelo.service.facade.UserFacadeService;
import ru.tw1.euchekavelo.service.domain.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ConfigUserApplicationService.class, initializers = ConfigDataApplicationContextInitializer.class)
class UserFacadeServiceTest {

    @Autowired
    private UserFacadeService userFacadeService;

    @Autowired
    private UserDomainService userDomainService;

    @Autowired
    private AuthServiceApiClient authServiceApiClient;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private UserSubscriptionDomainService userSubscriptionDomainService;

    private static UUID userId;
    private static User user;
    private static UserRequestDto userRequestDto;
    private static ShortUserRequestDto shortUserRequestDto;
    private static UserExternalResponseDto userExternalResponseDto;
    private static TokenResponseDto tokenResponseDto;

    @BeforeAll
    static void beforeAll() {
        userId = UUID.randomUUID();
        user = getUserWithId(userId);
        shortUserRequestDto = getShortUserRequestDto();
        userRequestDto = getUserRequestDto();
        userExternalResponseDto = getUserExternalResponseDto();
        tokenResponseDto = new TokenResponseDto();
    }

    @BeforeEach
    void resetMocks() {
        Mockito.reset(userDomainService);
        Mockito.reset(authServiceApiClient);
        Mockito.reset(authorizationService);
        Mockito.reset(authServiceApiClient);
        Mockito.reset(userSubscriptionDomainService);
    }

    @Test
    void createUserTestSuccess() {
        Mockito.when(userDomainService.saveUser(Mockito.any())).thenReturn(user);
        Mockito.when(authServiceApiClient.getAdminToken()).thenReturn(tokenResponseDto);
        Mockito.doNothing().when(authServiceApiClient).createUser(Mockito.any(), Mockito.any());
        UserResponseDto userResponseDto = userFacadeService.createUser(shortUserRequestDto);

        assertThat(userResponseDto.getId()).isNotNull();
    }


    @Test
    void getUserByIdTestSuccess() {
        Mockito.when(userDomainService.findUserById(userId)).thenReturn(user);

        UserResponseDto userResponseDto = userFacadeService.getUserById(userId);
        assertThat(userResponseDto.getEmail()).isEqualTo("invanov_test@gmail.com");
    }

    @Test
    void getUserByIdThrowUserNotFoundException() {
        Mockito.when(userDomainService.findUserById(userId)).thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class, () -> userFacadeService.getUserById(userId));
    }

    @Test
    void updateUserByIdTestSuccess() {
        Mockito.doNothing().when(authorizationService).checkAccess(userId);
        Mockito.when(userDomainService.findUserById(userId)).thenReturn(user);
        Mockito.when(authServiceApiClient.getAdminToken()).thenReturn(tokenResponseDto);
        Mockito.when(authServiceApiClient.getUsersByAttributeValue(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(Optional.of(userExternalResponseDto));

        Mockito.when(userDomainService.updateUser(user)).thenReturn(user);
        Mockito.doNothing().when(authServiceApiClient).updateUserById(Mockito.any(), Mockito.any(), Mockito.any());

        assertThat(userFacadeService.updateUserById(userId, userRequestDto).getEmail())
                .isEqualTo("invanov_test@gmail.com");
    }

    @Test
    void updateUserByIdTestThrowUserNotFoundException() {
        UserRequestDto userRequestDto = getUserRequestDto();
        Mockito.when(userDomainService.findUserById(userId)).thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class, () -> userFacadeService.updateUserById(userId, userRequestDto));
    }

    @Test
    void updateUserByIdTestThrowResourceAccessDeniedException() {
        Mockito.doThrow(ResourceAccessDeniedException.class).when(authorizationService).checkAccess(userId);

        assertThrows(ResourceAccessDeniedException.class,
                () -> userFacadeService.updateUserById(userId, userRequestDto));
    }

    @Test
    void updateUserByIdTestThrowUserNotFoundExceptionFromApiClient() {
        Mockito.when(authServiceApiClient.getAdminToken()).thenReturn(tokenResponseDto);
        Mockito.when(authServiceApiClient.getUsersByAttributeValue(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userFacadeService.updateUserById(userId, userRequestDto));
    }

    @Test
    void deleteUserByIdTestSuccess() {
        Mockito.when(authServiceApiClient.getAdminToken()).thenReturn(tokenResponseDto);
        Mockito.when(authServiceApiClient.getUsersByAttributeValue(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(Optional.of(userExternalResponseDto));
        Mockito.doNothing().when(userDomainService).deleteUserById(userId);

        assertDoesNotThrow(() -> userFacadeService.deleteUserById(userId));
    }

    @Test
    void deleteUserByIdTestThrowUserNotFoundException() {
        Mockito.doThrow(UserNotFoundException.class).when(userDomainService).deleteUserById(userId);

        assertThrows(UserNotFoundException.class, () -> userFacadeService.deleteUserById(userId));
    }

    @Test
    void deleteUserByIdTestResourceAccessDeniedException() {
        Mockito.doThrow(ResourceAccessDeniedException.class).when(authorizationService).checkAccess(userId);

        assertThrows(ResourceAccessDeniedException.class, () -> userFacadeService.deleteUserById(userId));
    }

    @Test
    void subscribeToUserTestSuccess() {
        UUID destinationUserId = UUID.fromString("15afa0c0-2fe3-47d9-916b-761e59b67caa");

        UserSubscriptionDto userSubscriptionDto = new UserSubscriptionDto();
        userSubscriptionDto.setSourceUserId(userId);
        userSubscriptionDto.setDestinationUserId(destinationUserId);

        User destinationUser = new User();
        destinationUser.setId(destinationUserId);
        destinationUser.setEmail("kirilov_test@gmail.com");
        destinationUser.setLastName("Kirilov Ivan Ivanovich");
        destinationUser.setSex(Sex.MALE);

        Mockito.when(userDomainService.findUserById(userId)).thenReturn(user);
        Mockito.when(userDomainService.findUserById(destinationUserId)).thenReturn(destinationUser);

        assertThat(userFacadeService.subscribeToUser(userSubscriptionDto).getMessage())
                .isEqualTo("The target user was successfully subscribed.");
    }

    @Test
    void subscribeToUserTestThrowUserSubscriptionExceptionWhenSubscribeToYourself() {
        UserSubscriptionDto userSubscriptionDto = new UserSubscriptionDto();
        userSubscriptionDto.setSourceUserId(userId);
        userSubscriptionDto.setDestinationUserId(userId);

        assertThrows(UserSubscriptionException.class, () -> userFacadeService.subscribeToUser(userSubscriptionDto));
    }

    @Test
    void subscribeToUserTestThrowUserNotFoundExceptionWhenSourceUserNotFound() {
        UUID destinationUserId = UUID.fromString("15afa0c0-2fe3-47d9-916b-761e59b67caa");
        User destinationUser = new User();
        destinationUser.setId(destinationUserId);
        destinationUser.setEmail("kirilov_test@gmail.com");
        destinationUser.setLastName("Kirilov Ivan Ivanovich");
        destinationUser.setSex(Sex.MALE);

        UserSubscriptionDto userSubscriptionDto = new UserSubscriptionDto();
        userSubscriptionDto.setSourceUserId(userId);
        userSubscriptionDto.setDestinationUserId(destinationUserId);

        Mockito.when(userDomainService.findUserById(userId)).thenThrow(UserNotFoundException.class);
        Mockito.when(userDomainService.findUserById(destinationUserId)).thenReturn(destinationUser);

        assertThrows(UserNotFoundException.class, () -> userFacadeService.subscribeToUser(userSubscriptionDto));
    }

    @Test
    void subscribeToUserTestThrowUserNotFoundExceptionWhenDestinationUserNotFound() {
        UUID destinationUserId = UUID.fromString("15afa0c0-2fe3-47d9-916b-761e59b67caa");

        UserSubscriptionDto userSubscriptionDto = new UserSubscriptionDto();
        userSubscriptionDto.setSourceUserId(userId);
        userSubscriptionDto.setDestinationUserId(destinationUserId);

        Mockito.when(userDomainService.findUserById(userId)).thenReturn(user);
        Mockito.when(userDomainService.findUserById(destinationUserId)).thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class, () -> userFacadeService.subscribeToUser(userSubscriptionDto));
    }

    @Test
    void subscribeToUserTestThrowUserSubscriptionExceptionWhenExists() {
        UUID destinationUserId = UUID.fromString("15afa0c0-2fe3-47d9-916b-761e59b67caa");

        UserSubscriptionDto userSubscriptionDto = new UserSubscriptionDto();
        userSubscriptionDto.setSourceUserId(userId);
        userSubscriptionDto.setDestinationUserId(destinationUserId);

        Mockito.when(userSubscriptionDomainService.createUserSubscription(Mockito.any(), Mockito.any()))
                .thenThrow(UserSubscriptionException.class);

        assertThrows(UserSubscriptionException.class, () -> userFacadeService.subscribeToUser(userSubscriptionDto));
    }

    @Test
    void subscribeToUserTestThrowResourceAccessDeniedException() {
        UUID destinationUserId = UUID.fromString("15afa0c0-2fe3-47d9-916b-761e59b67caa");

        UserSubscriptionDto userSubscriptionDto = new UserSubscriptionDto();
        userSubscriptionDto.setSourceUserId(userId);
        userSubscriptionDto.setDestinationUserId(destinationUserId);

        Mockito.doThrow(ResourceAccessDeniedException.class).when(authorizationService).checkAccess(userId);

        assertThrows(ResourceAccessDeniedException.class,
                () -> userFacadeService.subscribeToUser(userSubscriptionDto));
    }

    @Test
    void unsubscribeFromUserTestSuccess() {
        UUID destinationUserId = UUID.fromString("15afa0c0-2fe3-47d9-916b-761e59b67caa");

        UserSubscriptionDto userSubscriptionDto = new UserSubscriptionDto();
        userSubscriptionDto.setSourceUserId(userId);
        userSubscriptionDto.setDestinationUserId(destinationUserId);

        User destinationUser = new User();
        destinationUser.setId(destinationUserId);
        destinationUser.setEmail("kirilov_test@gmail.com");
        destinationUser.setLastName("Kirilov Ivan Ivanovich");
        destinationUser.setSex(Sex.MALE);

        Mockito.when(userDomainService.findUserById(userId)).thenReturn(user);
        Mockito.when(userDomainService.findUserById(destinationUserId)).thenReturn(destinationUser);

        assertDoesNotThrow(() -> userFacadeService.unsubscribeFromUser(userSubscriptionDto));
    }

    @Test
    void unsubscribeFromUserTestThrowUserSubscriptionExceptionWhenUserUnsubscribeYourself() {
        UserSubscriptionDto userSubscriptionDto = new UserSubscriptionDto();
        userSubscriptionDto.setSourceUserId(userId);
        userSubscriptionDto.setDestinationUserId(userId);

        assertThrows(UserSubscriptionException.class,
                () -> userFacadeService.unsubscribeFromUser(userSubscriptionDto));
    }

    @Test
    void unsubscribeFromUserTestThrowUserSubscriptionExceptionWhenSubscriptionNotExists() {
        UUID destinationUserId = UUID.fromString("15afa0c0-2fe3-47d9-916b-761e59b67caa");

        UserSubscriptionDto userSubscriptionDto = new UserSubscriptionDto();
        userSubscriptionDto.setSourceUserId(userId);
        userSubscriptionDto.setDestinationUserId(destinationUserId);

        Mockito.doThrow(UserSubscriptionException.class).when(userSubscriptionDomainService)
                .deleteUserSubscriptionBySourceUserIdAndDestinationUserId(userId, destinationUserId);

        assertThrows(UserSubscriptionException.class,
                () -> userFacadeService.unsubscribeFromUser(userSubscriptionDto));
    }

    @Test
    void unsubscribeFromUserTestThrowResourceAccessDeniedException() {
        UUID destinationUserId = UUID.fromString("15afa0c0-2fe3-47d9-916b-761e59b67caa");

        UserSubscriptionDto userSubscriptionDto = new UserSubscriptionDto();
        userSubscriptionDto.setSourceUserId(userId);
        userSubscriptionDto.setDestinationUserId(destinationUserId);

        Mockito.doThrow(ResourceAccessDeniedException.class).when(authorizationService).checkAccess(userId);

        assertThrows(ResourceAccessDeniedException.class,
                () -> userFacadeService.unsubscribeFromUser(userSubscriptionDto));
    }

    private static UserExternalResponseDto getUserExternalResponseDto() {
        UserExternalResponseDto userExternalResponseDto = new UserExternalResponseDto();
        userExternalResponseDto.setFirstName("Ivan");
        userExternalResponseDto.setLastName("Ivanov");
        userExternalResponseDto.setUsername("ivanov_test@gmail.com");
        userExternalResponseDto.setEmail("invanov_test@gmail.com");
        userExternalResponseDto.setId(UUID.randomUUID());
        userExternalResponseDto.setCreatedTimestamp(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        userExternalResponseDto.setEmailVerified(true);
        userExternalResponseDto.setEnabled(true);

        return userExternalResponseDto;
    }

    private static User getUserWithId(UUID id) {
        User user = new User();
        user.setId(id);
        user.setEmail("invanov_test@gmail.com");
        user.setLastName("Ivanov");
        user.setFirstName("Ivan");
        user.setMiddleName("Ivanovich");
        user.setSex(Sex.MALE);

        return user;
    }

    private static ShortUserRequestDto getShortUserRequestDto() {
        ShortUserRequestDto shortUserRequestDto = new ShortUserRequestDto();
        shortUserRequestDto.setFirstName("Ivanov");
        shortUserRequestDto.setLastName("Ivanovich");
        shortUserRequestDto.setMiddleName("Ivanovich");
        shortUserRequestDto.setEmail("invanov_test@gmail.com");
        shortUserRequestDto.setPassword("password");
        shortUserRequestDto.setSex(Sex.MALE.name());

        return shortUserRequestDto;
    }

    private static UserRequestDto getUserRequestDto() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setFirstName("Ivan");
        userRequestDto.setLastName("Ivanov");
        userRequestDto.setMiddleName("Ivanovich");
        userRequestDto.setEmail("invanov_test@gmail.com");
        userRequestDto.setSex(Sex.MALE.name());
        userRequestDto.setBirthDate(LocalDate.of(1990, 1, 1));
        userRequestDto.setPhone("+7123456789");

        return userRequestDto;
    }
}
