package ru.tw1.euchekavelo.userservice.controller;

import io.micrometer.observation.annotation.Observed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.tw1.euchekavelo.userservice.dto.request.UserSubscriptionDto;
import ru.tw1.euchekavelo.userservice.dto.response.ResponseDto;
import ru.tw1.euchekavelo.userservice.service.UserSubscriptionService;

@Tag(name="Контроллер по работе с подписками пользователей", description="Спецификация API микросервиса " +
        "по работе с подписками пользователей.")
@RestController
@RequestMapping("/users/subscriptions")
@RequiredArgsConstructor
public class UserSubscriptionController {

    private final UserSubscriptionService userSubscriptionService;

    @PreAuthorize("hasAnyAuthority('users_viewer', 'users_admin')")
    @Observed(contextualName = "Tracing subscribeToUser method controller")
    @Operation(summary = "Создать подписку между пользователями.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
            }),
            @ApiResponse(responseCode = "500", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
            })
    })
    @PostMapping
    public ResponseEntity<Void> subscribeToUser(@RequestBody UserSubscriptionDto userSubscriptionDto) {
        userSubscriptionService.subscribeToUser(userSubscriptionDto.getSourceUserId(),
                userSubscriptionDto.getDestinationUserId());

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyAuthority('users_viewer', 'users_admin')")
    @Observed(contextualName = "Tracing unsubscribeFromUser method controller")
    @Operation(summary = "Отменить подписку между пользователями.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
            }),
            @ApiResponse(responseCode = "500", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
            })
    })
    @DeleteMapping
    public ResponseEntity<Void> unsubscribeFromUser(@RequestBody UserSubscriptionDto userSubscriptionDto) {
        userSubscriptionService.unsubscribeFromUser(userSubscriptionDto.getSourceUserId(),
                userSubscriptionDto.getDestinationUserId());

        return ResponseEntity.noContent().build();
    }
}
