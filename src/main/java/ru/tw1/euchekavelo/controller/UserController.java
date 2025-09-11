package ru.tw1.euchekavelo.controller;

import io.micrometer.observation.annotation.Observed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.tw1.euchekavelo.dto.request.ShortUserRequestDto;
import ru.tw1.euchekavelo.dto.request.UserRequestDto;
import ru.tw1.euchekavelo.dto.response.ResponseDto;
import ru.tw1.euchekavelo.dto.request.UserSubscriptionDto;
import ru.tw1.euchekavelo.dto.response.UserResponseDto;
import ru.tw1.euchekavelo.service.facade.UserFacadeService;

import java.util.UUID;

@Tag(name="Контроллер по работе с пользователями", description="Спецификация API микросервиса по работе пользователями.")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserFacadeService userFacadeService;

    @SecurityRequirements
    @Observed(contextualName = "Tracing createUser method controller")
    @Operation(summary = "Создать пользователя.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))
            }),
            @ApiResponse(responseCode = "400", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
            }),
            @ApiResponse(responseCode = "500", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
            })
    })
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody ShortUserRequestDto shortUserRequestDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(userFacadeService.createUser(shortUserRequestDto));
    }

    @SecurityRequirements
    @Observed(contextualName = "Tracing getUserById method controller")
    @Operation(summary = "Получить информацию о пользователе.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
            }),
            @ApiResponse(responseCode = "500", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
            })
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@Parameter(description = "ID пользователя.") @PathVariable UUID id) {
        return ResponseEntity.ok(userFacadeService.getUserById(id));
    }

    @PreAuthorize("hasAnyAuthority('users_viewer', 'users_admin')")
    @Observed(contextualName = "Tracing updateUserById method controller")
    @Operation(summary = "Обновить информацию о пользователе.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))
            }),
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
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUserById(@Parameter(description = "ID пользователя.")
                                                          @PathVariable UUID id, @Valid @RequestBody UserRequestDto userRequestDto) {

        return ResponseEntity.ok(userFacadeService.updateUserById(id, userRequestDto));
    }

    @PreAuthorize("hasAnyAuthority('users_viewer', 'users_admin')")
    @Observed(contextualName = "Tracing deleteUserById method controller")
    @Operation(summary = "Удалить пользователя.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "404", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
            }),
            @ApiResponse(responseCode = "500", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
            })
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@Parameter(description = "ID пользователя.") @PathVariable UUID id) {
        userFacadeService.deleteUserById(id);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyAuthority('users_viewer', 'users_admin')")
    @Observed(contextualName = "Tracing subscribeToUser method controller")
    @Operation(summary = "Создать подписку между пользователями.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
            }),
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
    @PostMapping("/subscription")
    public ResponseEntity<ResponseDto> subscribeToUser(@RequestBody UserSubscriptionDto userSubscriptionDto) {
        return ResponseEntity.ok(userFacadeService.subscribeToUser(userSubscriptionDto));
    }

    @PreAuthorize("hasAnyAuthority('users_viewer', 'users_admin')")
    @Observed(contextualName = "Tracing unsubscribeFromUser method controller")
    @Operation(summary = "Отменить подписку между пользователями.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
            }),
            @ApiResponse(responseCode = "400", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
            }),
            @ApiResponse(responseCode = "500", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
            })
    })
    @DeleteMapping("/subscription")
    public ResponseEntity<Void> unsubscribeFromUser(@RequestBody UserSubscriptionDto userSubscriptionDto) {
        userFacadeService.unsubscribeFromUser(userSubscriptionDto);

        return ResponseEntity.noContent().build();
    }
}
