package ru.tw1.euchekavelo.userservice.adapter.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import ru.tw1.euchekavelo.userservice.dto.external.request.CredentialsRequestDto;
import ru.tw1.euchekavelo.userservice.dto.external.request.UserRepresentationRequestDto;
import ru.tw1.euchekavelo.userservice.dto.external.response.TokenResponseDto;
import ru.tw1.euchekavelo.userservice.dto.external.response.UserExternalResponseDto;
import ru.tw1.euchekavelo.userservice.exception.ExternalServiceException;
import ru.tw1.euchekavelo.userservice.mapper.UserMapper;
import ru.tw1.euchekavelo.userservice.model.User;

import java.util.*;

@Component
public class AuthServiceApiAdapter {

    private final RestClient restClient;
    private final UserMapper userMapper;

    @Value("${auth-service.api.realm-name}")
    private String realmName;

    @Value("${auth-service.api.admin-client-id}")
    private String adminClientId;

    @Value("${auth-service.api.username}")
    private String adminUsername;

    @Value("${auth-service.api.password}")
    private String adminPassword;

    @Autowired
    public AuthServiceApiAdapter(RestClient.Builder restClientBuilder,
                                 @Value("${auth-service.api.root-url}") String baseUrl,
                                 UserMapper userMapper) {

        this.userMapper = userMapper;
        this.restClient = restClientBuilder
                .baseUrl(baseUrl)
                .build();
    }

    public void createUser(User user) {
        String accessAdminToken = getAdminToken();
        UserRepresentationRequestDto userRepresentationRequestDto
                = userMapper.userToUserRepresentationRequestDto(user);
        CredentialsRequestDto credentialsRequestDto = new CredentialsRequestDto();
        credentialsRequestDto.setTemporary(false);
        credentialsRequestDto.setType("password");
        credentialsRequestDto.setValue(user.getPassword());
        userRepresentationRequestDto.setCredentials(List.of(credentialsRequestDto));

        try {
            restClient.post()
                    .uri("/admin/realms/{realmName}/users", realmName)
                    .header("Authorization", "Bearer " + accessAdminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(userRepresentationRequestDto)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            throw new ExternalServiceException("Произошла ошибка при попытке создать внешнего пользователя: "
                    + e.getMessage());
        }
    }

    public User getUserById(UUID id) {
        String accessAdminToken = getAdminToken();

        try {
            UserExternalResponseDto userExternalResponseDto = Arrays.stream(Objects.requireNonNull(restClient.get()
                            .uri(uriBuilder -> uriBuilder.path("/admin/realms/{realmName}/users")
                                    .queryParam("briefRepresentation", "true")
                                    .queryParam("q", "{attributeName}:{attributeValue}")
                                    .build(realmName, "user_id", id.toString()))
                            .header("Authorization", "Bearer " + accessAdminToken)
                            .retrieve()
                            .toEntity(UserExternalResponseDto[].class)
                            .getBody()))
                    .findFirst().get();

            return userMapper.userExternalResponseDtoToUser(userExternalResponseDto);
        } catch (Exception e) {
            throw new ExternalServiceException("Произошла ошибка при попытке получить информацию о внешнем пользователе: "
                    + e.getMessage());
        }
    }

    public void updateUserById(UUID userId, User user) {
        String accessAdminToken = getAdminToken();
        UserRepresentationRequestDto userRepresentationRequestDto
                = userMapper.userToUserRepresentationRequestDto(user);
        userRepresentationRequestDto.setCredentials(Collections.emptyList());

        try {
            restClient.put()
                    .uri("/admin/realms/{realmName}/users/{userId}", realmName, userId)
                    .header("Authorization", "Bearer " + accessAdminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(userRepresentationRequestDto)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            throw new ExternalServiceException("Произошла ошибка при попытке обновить данные внешнего пользователя: "
                    + e.getMessage());
        }
    }

    public void deleteUserById(UUID userId) {
        String accessAdminToken = getAdminToken();

        try {
            restClient.delete()
                    .uri("/admin/realms/{realmName}/users/{userId}", realmName, userId)
                    .header("Authorization", "Bearer " + accessAdminToken)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            throw new ExternalServiceException("Произошла ошибка при попытке удалить внешнего пользователя: "
                    + e.getMessage());
        }
    }

    private String getAdminToken() {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", adminClientId);
        formData.add("username", adminUsername);
        formData.add("password", adminPassword);
        formData.add("grant_type", "password");

        try {
            return Objects.requireNonNull(restClient.post()
                            .uri("/realms/master/protocol/openid-connect/token")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .body(formData)
                            .retrieve()
                            .body(TokenResponseDto.class))
                    .getAccessToken();
        } catch (Exception e) {
            throw new ExternalServiceException("Произошла ошибка при попытке получить токен администратора от " +
                    "внешнего сервиса: " + e.getMessage());
        }
    }
}
