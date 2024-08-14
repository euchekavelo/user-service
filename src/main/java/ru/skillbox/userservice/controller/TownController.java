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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.userservice.dto.response.ResponseDto;
import ru.skillbox.userservice.dto.TownDto;
import ru.skillbox.userservice.dto.response.TownResponseDto;
import ru.skillbox.userservice.exception.TownNotFoundException;
import ru.skillbox.userservice.service.TownService;

import java.util.UUID;

@Tag(name="Контроллер по работе с городами", description="Спецификация API микросервиса по работе с городами.")
@RestController
@RequestMapping("/towns")
public class TownController {

    private final TownService townService;

    @Autowired
    public TownController(TownService townService) {
        this.townService = townService;
    }

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
    public ResponseEntity<TownResponseDto> createTown(@Valid @RequestBody TownDto townDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(townService.createTown(townDto));
    }

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
    public ResponseEntity<TownResponseDto> getTownById(@Parameter(description = "ID города.") @PathVariable UUID id)
            throws TownNotFoundException {

        return ResponseEntity.ok(townService.getTownById(id));
    }

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
    public ResponseEntity<Void> deleteTownById(@Parameter(description = "ID города.") @PathVariable UUID id)
            throws TownNotFoundException {

        townService.deleteTownById(id);

        return ResponseEntity.noContent().build();
    }

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
                                                          @Valid @RequestBody TownDto townDto)
            throws TownNotFoundException {

        return ResponseEntity.ok(townService.updateTownById(id, townDto));
    }
}
