package ru.tw1.euchekavelo.client.api;

import ru.tw1.euchekavelo.dto.external.request.UserRepresentationRequestDto;
import ru.tw1.euchekavelo.dto.external.response.TokenResponseDto;
import ru.tw1.euchekavelo.dto.external.response.UserExternalResponseDto;

import java.util.Optional;
import java.util.UUID;

public interface AuthServiceApiClient {

    void createUser(UserRepresentationRequestDto userRepresentationRequestDto, String accessToken);

    Optional<UserExternalResponseDto> getUsersByAttributeValue(String attributeName, String attributeValue, String accessToken);

    void updateUserById(UUID userId, UserRepresentationRequestDto userRepresentationRequestDto, String accessToken);

    void deleteUserById(UUID userId, String accessToken);

    TokenResponseDto getAdminToken();
}
