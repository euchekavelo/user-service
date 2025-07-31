package ru.tw1.euchekavelo.client.api;

import ru.tw1.euchekavelo.dto.external.request.UserRepresentationRequestDto;
import ru.tw1.euchekavelo.dto.external.response.UserExternalResponseDto;

import java.util.Optional;
import java.util.UUID;

public interface AuthServiceApiClient {

    void createUser(UserRepresentationRequestDto userRepresentationRequestDto);

    Optional<UserExternalResponseDto> getUsersByAttributeValue(String attributeName, String attributeValue);

    void updateUserById(UUID userId, UserRepresentationRequestDto userRepresentationRequestDto);
}
