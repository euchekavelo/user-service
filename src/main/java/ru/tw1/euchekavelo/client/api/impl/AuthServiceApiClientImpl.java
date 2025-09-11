package ru.tw1.euchekavelo.client.api.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import ru.tw1.euchekavelo.client.api.AuthServiceApiClient;
import ru.tw1.euchekavelo.dto.external.request.UserRepresentationRequestDto;
import ru.tw1.euchekavelo.dto.external.response.TokenResponseDto;
import ru.tw1.euchekavelo.dto.external.response.UserExternalResponseDto;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
public class AuthServiceApiClientImpl implements AuthServiceApiClient {

    private final RestClient restClient;

    @Value("${auth-service.api.realm-name}")
    private String realmName;

    @Value("${auth-service.api.admin-client-id}")
    private String adminClientId;

    @Value("${auth-service.api.username}")
    private String adminUsername;

    @Value("${auth-service.api.password}")
    private String adminPassword;

    @Autowired
    public AuthServiceApiClientImpl(RestClient.Builder restClientBuilder,
                                    @Value("${auth-service.api.root-url}") String baseUrl) {

        this.restClient = restClientBuilder
                .baseUrl(baseUrl)
                .build();
    }

    @Override
    public void createUser(UserRepresentationRequestDto userRepresentationRequestDto, String accessToken) {
        restClient.post()
                .uri("/admin/realms/{realmName}/users", realmName)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(userRepresentationRequestDto)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public Optional<UserExternalResponseDto> getUsersByAttributeValue(String attributeName, String attributeValue,
                                                                      String accessToken) {

       return Arrays.stream(Objects.requireNonNull(restClient.get()
               .uri(uriBuilder -> uriBuilder.path("/admin/realms/{realmName}/users")
                       .queryParam("briefRepresentation", "true")
                       .queryParam("q", "{attributeName}:{attributeValue}")
                       .build(realmName, attributeName, attributeValue))
               .header("Authorization", "Bearer " + accessToken)
               .retrieve()
               .toEntity(UserExternalResponseDto[].class)
               .getBody()))
               .findFirst();
    }

    @Override
    public void updateUserById(UUID userId, UserRepresentationRequestDto userRepresentationRequestDto, String accessToken) {
        restClient.put()
                .uri("/admin/realms/{realmName}/users/{userId}", realmName, userId)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(userRepresentationRequestDto)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void deleteUserById(UUID userId, String accessToken) {
        restClient.delete()
                .uri("/admin/realms/{realmName}/users/{userId}", realmName, userId)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public TokenResponseDto getAdminToken() {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", adminClientId);
        formData.add("username", adminUsername);
        formData.add("password", adminPassword);
        formData.add("grant_type", "password");

        return restClient.post()
                .uri("/realms/master/protocol/openid-connect/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formData)
                .retrieve()
                .body(TokenResponseDto.class);
    }
}
