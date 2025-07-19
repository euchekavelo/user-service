package ru.tw1.euchekavelo.service.application;

import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tw1.euchekavelo.client.api.AuthServiceApiClient;
import ru.tw1.euchekavelo.dto.external.request.CredentialsRequestDto;
import ru.tw1.euchekavelo.dto.external.request.UserRepresentationRequestDto;
import ru.tw1.euchekavelo.dto.external.response.UserExternalResponseDto;
import ru.tw1.euchekavelo.dto.request.ShortUserRequestDto;
import ru.tw1.euchekavelo.dto.request.UserRequestDto;
import ru.tw1.euchekavelo.dto.response.ResponseDto;
import ru.tw1.euchekavelo.dto.request.UserSubscriptionDto;
import ru.tw1.euchekavelo.dto.response.UserResponseDto;
import ru.tw1.euchekavelo.exception.*;
import ru.tw1.euchekavelo.mapper.UserMapper;
import ru.tw1.euchekavelo.model.*;
import ru.tw1.euchekavelo.service.AuthorizationService;
import ru.tw1.euchekavelo.service.domain.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static ru.tw1.euchekavelo.exception.enums.ExceptionMessage.*;

@Observed
@Service
@RequiredArgsConstructor
public class UserApplicationService {

    private final UserDomainService userDomainService;
    private final UserGroupDomainService userGroupDomainService;
    private final UserSubscriptionDomainService userSubscriptionDomainService;
    private final TownDomainService townDomainService;
    private final GroupDomainService groupDomainService;
    private final UserMapper userMapper;
    private final AuthServiceApiClient authServiceApiClient;
    private final AuthorizationService authorizationService;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserApplicationService.class);

    @Transactional(rollbackFor = Throwable.class)
    public UserResponseDto createUser(ShortUserRequestDto shortUserRequestDto) {
        User user = userMapper.shortUserDtoToUser(shortUserRequestDto);
        User savedUser = userDomainService.saveUser(user);

        UserRepresentationRequestDto userRepresentationRequestDto
                = userMapper.userToUserRepresentationRequestDto(savedUser);
        CredentialsRequestDto credentialsRequestDto = new CredentialsRequestDto();
        credentialsRequestDto.setTemporary(false);
        credentialsRequestDto.setType("password");
        credentialsRequestDto.setValue(shortUserRequestDto.getPassword());
        userRepresentationRequestDto.setCredentials(List.of(credentialsRequestDto));
        authServiceApiClient.createUser(userRepresentationRequestDto);

        return userMapper.userToUserResponseDto(savedUser);
    }

    public UserResponseDto getUserById(UUID id) {
        authorizationService.checkAccess(id);
        User user = userDomainService.findUserById(id);

        return userMapper.userToUserResponseDto(user);
    }

    @Transactional
    public UserResponseDto updateUserById(UUID id, UserRequestDto userRequestDto) {
        authorizationService.checkAccess(id);

        User user = userDomainService.findUserById(id);
        Town town = townDomainService.findTownById(userRequestDto.getTownId());

        UserExternalResponseDto userExternalResponseDto
                = authServiceApiClient.getUsersByAttributeValue("user_id", id.toString())
                .orElseThrow(() -> {
                    LOGGER.error(USER_NOT_FOUND_EXCEPTION_MESSAGE.toString());
                    return new UserNotFoundException(USER_NOT_FOUND_EXCEPTION_MESSAGE.toString());
                });

        User updatedUserWithFields = userMapper.userDtoToUser(user, userRequestDto);
        updatedUserWithFields.setTown(town);
        User updatedUser = userDomainService.updateUser(updatedUserWithFields);

        UserRepresentationRequestDto userRepresentationRequestDto
                = userMapper.userToUserRepresentationRequestDto(updatedUser);
        userRepresentationRequestDto.setCredentials(Collections.emptyList());
        authServiceApiClient.updateUserById(userExternalResponseDto.getId(), userRepresentationRequestDto);

        return userMapper.userToUserResponseDto(updatedUser);
    }

    public void deleteUserById(UUID id) {
        authorizationService.checkAccess(id);
        userDomainService.deleteUserById(id);
    }

    public ResponseDto subscribeToUser(UserSubscriptionDto userSubscriptionDto) {
        UUID sourceUserId = userSubscriptionDto.getSourceUserId();
        authorizationService.checkAccess(sourceUserId);
        UUID destinationUserId = userSubscriptionDto.getDestinationUserId();

        if (sourceUserId.equals(destinationUserId)) {
            LOGGER.error(USER_SUBSCRIPTION_EXCEPTION_MESSAGE.getExceptionMessage());
            throw new UserSubscriptionException(USER_SUBSCRIPTION_EXCEPTION_MESSAGE.getExceptionMessage());
        }

        User sourceUser = userDomainService.findUserById(sourceUserId);
        User destinationUser = userDomainService.findUserById(destinationUserId);
        userSubscriptionDomainService.createUserSubscription(sourceUser, destinationUser);

        return getResponseDto("The target user was successfully subscribed.");
    }

    public ResponseDto unsubscribeFromUser(UserSubscriptionDto userSubscriptionDto) {
        UUID sourceUserId = userSubscriptionDto.getSourceUserId();
        authorizationService.checkAccess(userSubscriptionDto.getSourceUserId());
        UUID destinationUserId = userSubscriptionDto.getDestinationUserId();

        if (sourceUserId.equals(destinationUserId)) {
            LOGGER.error(USER_UNSUBSCRIBE_HIMSELF_EXCEPTION_MESSAGE.getExceptionMessage());
            throw new UserSubscriptionException(USER_UNSUBSCRIBE_HIMSELF_EXCEPTION_MESSAGE.getExceptionMessage());
        }

        userSubscriptionDomainService
                .deleteUserSubscriptionBySourceUserIdAndDestinationUserId(sourceUserId, destinationUserId);

        return getResponseDto("The destination user has been unsubscribed successfully.");
    }

    public ResponseDto addUserToGroup(UUID userId, UUID groupId) {
        authorizationService.checkAccess(userId);
        User user = userDomainService.findUserById(userId);
        Group group = groupDomainService.getGroupById(groupId);

        userGroupDomainService.createUserGroup(user, group);

        return getResponseDto("The user has been successfully added to the group.");
    }

    public ResponseDto deleteUserFromGroup(UUID userId, UUID groupId) {
        authorizationService.checkAccess(userId);
        userGroupDomainService.deleteUserGroupByUserIdAndGroupId(userId, groupId);

        return getResponseDto("The user has been successfully removed from the group.");
    }

    private ResponseDto getResponseDto(String message) {
        return ResponseDto.builder()
                .message(message)
                //.id(null)
                .result(true)
                .build();
    }
}
