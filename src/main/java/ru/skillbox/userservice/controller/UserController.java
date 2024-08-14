package ru.skillbox.userservice.controller;

import io.micrometer.observation.annotation.Observed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skillbox.userservice.dto.ShortUserDto;
import ru.skillbox.userservice.dto.UserDto;
import ru.skillbox.userservice.dto.response.ResponseDto;
import ru.skillbox.userservice.dto.UserSubscriptionDto;
import ru.skillbox.userservice.dto.response.UserPhotoResponseDto;
import ru.skillbox.userservice.dto.response.UserResponseDto;
import ru.skillbox.userservice.exception.*;
import ru.skillbox.userservice.service.PhotoService;
import ru.skillbox.userservice.service.UserService;

import java.io.IOException;
import java.util.UUID;

@Tag(name="Контроллер по работе с пользователями", description="Спецификация API микросервиса по работе пользователями.")
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final PhotoService photoService;

    @Autowired
    public UserController(UserService userService, PhotoService photoService) {
        this.userService = userService;
        this.photoService = photoService;
    }

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
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody ShortUserDto shortUserDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(shortUserDto));
    }

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
    public ResponseEntity<UserResponseDto> getUserById(@Parameter(description = "ID пользователя.") @PathVariable UUID id)
            throws UserNotFoundException {

        return ResponseEntity.ok(userService.getUserById(id));
    }

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
                                                          @PathVariable UUID id, @Valid @RequestBody UserDto userDto)
            throws UserNotFoundException, TownNotFoundException {

        return ResponseEntity.ok(userService.updateUserById(id, userDto));
    }

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
    public ResponseEntity<Void> deleteUserById(@Parameter(description = "ID пользователя.") @PathVariable UUID id)
            throws UserNotFoundException {

        userService.deleteUserById(id);

        return ResponseEntity.noContent().build();
    }

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
    public ResponseEntity<ResponseDto> subscribeToUser(@RequestBody UserSubscriptionDto userSubscriptionDto)
            throws UserSubscriptionException, UserNotFoundException {

        return ResponseEntity.ok(userService.subscribeToUser(userSubscriptionDto));
    }

    @Observed(contextualName = "Tracing unsubscribeFromUser method controller")
    @Operation(summary = "Отменить подписку между пользователями.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
            }),
            @ApiResponse(responseCode = "400", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
            }),
            @ApiResponse(responseCode = "500", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
            })
    })
    @PostMapping("/unsubscription")
    public ResponseEntity<ResponseDto> unsubscribeFromUser(@RequestBody UserSubscriptionDto userSubscriptionDto)
            throws UserSubscriptionException {

        return ResponseEntity.ok(userService.unsubscribeFromUser(userSubscriptionDto));
    }

    @Observed(contextualName = "Tracing addUserToGroup method controller")
    @Operation(summary = "Добавить пользователя в группу.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
            }),
            @ApiResponse(responseCode = "500", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
            })
    })
    @PostMapping("/{userId}/groups/{groupId}")
    public ResponseEntity<ResponseDto> addUserToGroup(@Parameter(description = "ID пользователя.")
                                                      @PathVariable UUID userId,
                                                      @Parameter(description = "ID группы.")
                                                      @PathVariable UUID groupId)
            throws UserNotFoundException, GroupNotFoundException {

        return ResponseEntity.ok(userService.addUserToGroup(userId, groupId));
    }

    @Observed(contextualName = "Tracing deleteUserFromGroup method controller")
    @Operation(summary = "Удалить пользователя из группы.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
            }),
            @ApiResponse(responseCode = "500", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
            })
    })
    @DeleteMapping("/{userId}/groups/{groupId}")
    public ResponseEntity<ResponseDto> deleteUserFromGroup(@Parameter(description = "ID пользователя.")
                                                           @PathVariable UUID userId,
                                                           @Parameter(description = "ID группы.")
                                                           @PathVariable UUID groupId)
            throws UserGroupNotFoundException {

        return ResponseEntity.ok(userService.deleteUserFromGroup(userId, groupId));
    }

    @Observed(contextualName = "Tracing createUserPhoto method controller")
    @Operation(summary = "Установить фотографию пользователя.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserPhotoResponseDto.class))
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
    @PostMapping(value = "/{userId}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserPhotoResponseDto> createUserPhoto(@Parameter(description = "ID пользователя.")
                                                                @PathVariable UUID userId,
                                                                @Parameter(description = "Файл для зугрузки.")
                                                                MultipartFile file)
            throws UserNotFoundException, IncorrectFileContentException, IncorrectFileFormatException, IOException {

        return ResponseEntity.status(HttpStatus.CREATED).body(photoService.createUserPhoto(userId, file));
    }

    @Observed(contextualName = "Tracing getUserPhotoById method controller")
    @Operation(summary = "Получить фотографию пользователя.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserPhotoResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
            }),
            @ApiResponse(responseCode = "500", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
            })
    })
    @GetMapping("/{userId}/photos/{photoId}")
    public ResponseEntity<UserPhotoResponseDto> getUserPhotoById(@Parameter(description = "ID пользователя.")
                                                                 @PathVariable UUID userId,
                                                                 @Parameter(description = "ID фотографии.")
                                                                 @PathVariable UUID photoId)
            throws PhotoNotFoundException {

        return ResponseEntity.ok(photoService.getUserPhotoById(userId, photoId));
    }

    @Observed(contextualName = "Tracing deleteUserPhotoById method controller")
    @Operation(summary = "Удалить фотографию пользователя.")
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
    @DeleteMapping("/{userId}/photos/{photoId}")
    public ResponseEntity<Void> deleteUserPhotoById(@Parameter(description = "ID пользователя.")
                                                    @PathVariable UUID userId,
                                                    @Parameter(description = "ID фотографии.")
                                                    @PathVariable UUID photoId)
            throws PhotoNotFoundException, IOException {

        photoService.deleteUserPhotoById(userId, photoId);

        return ResponseEntity.noContent().build();
    }
}
