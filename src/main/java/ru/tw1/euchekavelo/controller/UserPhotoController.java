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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.tw1.euchekavelo.dto.response.ResponseDto;
import ru.tw1.euchekavelo.dto.response.UserPhotoResponseDto;
import ru.tw1.euchekavelo.service.application.PhotoApplicationService;

import java.util.UUID;

@Tag(name="Контроллер по работе с фотографиями пользователей", description="Спецификация API микросервиса по работе " +
        "с фотографиями пользователями.")
@RestController
@RequestMapping("/users/{userId}/photos")
@RequiredArgsConstructor
public class UserPhotoController {

    private final PhotoApplicationService photoApplicationService;

    @PreAuthorize("hasAnyAuthority('users_viewer', 'users_admin')")
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
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserPhotoResponseDto> createUserPhoto(@Parameter(description = "ID пользователя.")
                                                                @PathVariable UUID userId,
                                                                @Parameter(description = "Файл для загрузки.")
                                                                MultipartFile file) {

        return ResponseEntity.status(HttpStatus.CREATED).body(photoApplicationService.createUserPhoto(userId, file));
    }

    @SecurityRequirements
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
    @GetMapping("/{photoId}")
    public ResponseEntity<UserPhotoResponseDto> getUserPhotoById(@Parameter(description = "ID пользователя.")
                                                                 @PathVariable UUID userId,
                                                                 @Parameter(description = "ID фотографии.")
                                                                 @PathVariable UUID photoId) {

        return ResponseEntity.ok(photoApplicationService.getUserPhotoById(userId, photoId));
    }

    @PreAuthorize("hasAnyAuthority('users_viewer', 'users_admin')")
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
    @DeleteMapping("/{photoId}")
    public ResponseEntity<Void> deleteUserPhotoById(@Parameter(description = "ID пользователя.")
                                                    @PathVariable UUID userId,
                                                    @Parameter(description = "ID фотографии.")
                                                    @PathVariable UUID photoId) {

        photoApplicationService.deleteUserPhotoById(userId, photoId);

        return ResponseEntity.noContent().build();
    }
}
