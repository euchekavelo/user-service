package ru.tw1.euchekavelo.client.api.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import ru.tw1.euchekavelo.client.api.AuthServiceApiClient;
import ru.tw1.euchekavelo.dto.external.request.UserRepresentationRequestDto;
import ru.tw1.euchekavelo.dto.external.response.TokenResponseDto;
import ru.tw1.euchekavelo.dto.external.response.UserExternalResponseDto;
import ru.tw1.euchekavelo.exception.AuthServiceException;

import java.util.Optional;
import java.util.UUID;

@Component
public class AuthServiceApiClientImpl implements AuthServiceApiClient {

    private final WebClient webClient;

    @Value("${auth-service.api.realm-name}")
    private String realmName;

    @Value("${auth-service.api.admin-client-id}")
    private String adminClientId;

    @Value("${auth-service.api.username}")
    private String adminUsername;

    @Value("${auth-service.api.password}")
    private String adminPassword;

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthServiceApiClientImpl.class);

    @Autowired
    public AuthServiceApiClientImpl(WebClient.Builder webClientBuilder,
                                    @Value("${auth-service.api.root-url}") String baseUrl) {

        this.webClient = webClientBuilder
                .baseUrl(baseUrl)
                .build();
    }

    @Override
    public void createUser(UserRepresentationRequestDto userRepresentationRequestDto) {
        TokenResponseDto tokenResponseDto = getAdminToken();
        webClient.post()
                .uri("/admin/realms/{realmName}/users", realmName)
                .header("Authorization", "Bearer " + tokenResponseDto.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userRepresentationRequestDto)
                .retrieve()
                .toBodilessEntity()
                .onErrorResume(this::handleThrowable)
                .block();
    }

    @Override
    public Optional<UserExternalResponseDto> getUsersByAttributeValue(String attributeName, String attributeValue) {
        TokenResponseDto tokenResponseDto = getAdminToken();
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/admin/realms/{realmName}/users")
                        .queryParam("briefRepresentation", "true")
                        .queryParam("q", "{attributeName}:{attributeValue}")
                        .build(realmName, attributeName, attributeValue))
                .header("Authorization", "Bearer " + tokenResponseDto.getAccessToken())
                .retrieve()
                .bodyToFlux(UserExternalResponseDto.class)
                .next()
                .onErrorResume(this::handleThrowable)
                .blockOptional();
    }

    @Override
    public void updateUserById(UUID userId, UserRepresentationRequestDto userRepresentationRequestDto) {
        TokenResponseDto tokenResponseDto = getAdminToken();
        webClient.put()
                .uri("/admin/realms/{realmName}/users/{userId}", realmName, userId)
                .header("Authorization", "Bearer " + tokenResponseDto.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userRepresentationRequestDto)
                .retrieve()
                .toBodilessEntity()
                .onErrorResume(this::handleThrowable)
                .block();
    }

    private TokenResponseDto getAdminToken() {
        return webClient.post()
                .uri("/realms/master/protocol/openid-connect/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("client_id", adminClientId)
                        .with("username", adminUsername)
                        .with("password", adminPassword)
                        .with("grant_type", "password")
                )
                .retrieve()
                .bodyToMono(TokenResponseDto.class)
                .onErrorResume(this::handleThrowable)
                .block();
    }

    private <T> Mono<T> handleThrowable(Throwable throwable) {
        if (throwable instanceof WebClientRequestException) {
            return Mono.error(new AuthServiceException("При попытке отправить запрос к сервису авторизации возникла ошибка: "
                    + throwable.getMessage()));
        } else if (throwable instanceof WebClientResponseException) {
            return Mono.error(new AuthServiceException("При попытке получить ответ от сервиса авторизации возникла ошибка: "
                    + throwable.getMessage()));
        } else {
            return Mono.error(new AuthServiceException("Возникла иная ошибка: " + throwable.getMessage()));
        }
    }
}
