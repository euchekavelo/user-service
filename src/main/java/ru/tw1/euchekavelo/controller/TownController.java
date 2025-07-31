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
import ru.tw1.euchekavelo.dto.request.TownRequestDto;
import ru.tw1.euchekavelo.dto.response.ResponseDto;
import ru.tw1.euchekavelo.dto.response.TownResponseDto;
import ru.tw1.euchekavelo.service.application.TownApplicationService;

import java.util.UUID;

@Tag(name="Контроллер по работе с городами", description="Спецификация API микросервиса по работе с городами.")
@RestController
@RequestMapping("/towns")
@RequiredArgsConstructor
public class TownController {

    private final TownApplicationService townApplicationService;

    @PreAuthorize("hasAuthority('users_admin')")
    @Observed(contextualName = "Tracing createTown method controller")
    @Operation(summary = "Создать город.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = TownResponseDto.class))
            }),
            @ApiResponse(responseCode = "400", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
            }),
            @ApiResponse(responseCode = "500", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
            })
    })
    @PostMapping
    public ResponseEntity<TownResponseDto> createTown(@Valid @RequestBody TownRequestDto townRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(townApplicationService.createTown(townRequestDto));
    }

    @SecurityRequirements
    @Observed(contextualName = "Tracing getTownById method controller")
    @Operation(summary = "Получить информацию о городе.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = TownResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
            }),
            @ApiResponse(responseCode = "500", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
            })
    })
    @GetMapping("/{id}")
    public ResponseEntity<TownResponseDto> getTownById(@Parameter(description = "ID города.") @PathVariable UUID id) {
        return ResponseEntity.ok(townApplicationService.getTownById(id));
    }

    @PreAuthorize("hasAuthority('users_admin')")
    @Observed(contextualName = "Tracing deleteTownById method controller")
    @Operation(summary = "Удалить город.")
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
    public ResponseEntity<Void> deleteTownById(@Parameter(description = "ID города.") @PathVariable UUID id) {
        townApplicationService.deleteTownById(id);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('users_admin')")
    @Observed(contextualName = "Tracing updateTownById method controller")
    @Operation(summary = "Обновить информацию о городе.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = TownResponseDto.class))
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
    public ResponseEntity<TownResponseDto> updateTownById(@Parameter(description = "ID города.") @PathVariable UUID id,
                                                          @Valid @RequestBody TownRequestDto townRequestDto) {

        return ResponseEntity.ok(townApplicationService.updateTownById(id, townRequestDto));
    }
}
