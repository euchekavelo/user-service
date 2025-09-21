package ru.tw1.euchekavelo.service.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tw1.euchekavelo.client.api.AuthServiceApiClient;
import ru.tw1.euchekavelo.dto.external.request.CredentialsRequestDto;
import ru.tw1.euchekavelo.dto.external.request.UserRepresentationRequestDto;
import ru.tw1.euchekavelo.dto.external.response.UserExternalResponseDto;
import ru.tw1.euchekavelo.exception.UserNotFoundException;
import ru.tw1.euchekavelo.mapper.UserMapper;
import ru.tw1.euchekavelo.model.User;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static ru.tw1.euchekavelo.exception.enums.ExceptionMessage.USER_NOT_FOUND_EXCEPTION_MESSAGE;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiAuthService {

    private final AuthServiceApiClient authServiceApiClient;
    private final UserMapper userMapper;

    public void registerUser(User user) {
        String accessAdminToken = authServiceApiClient.getAdminToken().getAccessToken();
        UserRepresentationRequestDto userRepresentationRequestDto
                = userMapper.userToUserRepresentationRequestDto(user);
        CredentialsRequestDto credentialsRequestDto = new CredentialsRequestDto();
        credentialsRequestDto.setTemporary(false);
        credentialsRequestDto.setType("password");
        credentialsRequestDto.setValue(user.getPassword());
        userRepresentationRequestDto.setCredentials(List.of(credentialsRequestDto));

        authServiceApiClient.createUser(userRepresentationRequestDto, accessAdminToken);
    }

    public void updateUserById(UUID id, User user) {
        String accessAdminToken = authServiceApiClient.getAdminToken().getAccessToken();
        UserRepresentationRequestDto userRepresentationRequestDto
                = userMapper.userToUserRepresentationRequestDto(user);
        userRepresentationRequestDto.setCredentials(Collections.emptyList());

        authServiceApiClient.updateUserById(id, userRepresentationRequestDto, accessAdminToken);
    }

    public User getUserById(UUID id) {
        String accessAdminToken = authServiceApiClient.getAdminToken().getAccessToken();

        UserExternalResponseDto userExternalResponseDto = authServiceApiClient
                .getUsersByAttributeValue("user_id", id.toString(), accessAdminToken)
                .orElseThrow(() -> {
                    log.error(USER_NOT_FOUND_EXCEPTION_MESSAGE.toString());
                    return new UserNotFoundException(USER_NOT_FOUND_EXCEPTION_MESSAGE + " во внешней системе.");
                });

        return userMapper.userExternalResponseDtoToUser(userExternalResponseDto);
    }

    public void deleteUserById(UUID id) {
        String accessAdminToken = authServiceApiClient.getAdminToken().getAccessToken();

        authServiceApiClient.deleteUserById(id, accessAdminToken);
    }
}
