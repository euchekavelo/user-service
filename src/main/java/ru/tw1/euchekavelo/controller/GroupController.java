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
import ru.tw1.euchekavelo.dto.request.GroupRequestDto;
import ru.tw1.euchekavelo.dto.response.GroupResponseDto;
import ru.tw1.euchekavelo.dto.response.ResponseDto;
import ru.tw1.euchekavelo.service.application.GroupApplicationService;

import java.util.UUID;

@Tag(name="Контроллер по работе с группами", description="Спецификация API микросервиса по работе с группами.")
@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupApplicationService groupApplicationService;

    @PreAuthorize("hasAnyAuthority('users_viewer', 'users_admin')")
    @Observed(contextualName = "Tracing createGroup method controller")
    @Operation(summary = "Создать группу.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = GroupResponseDto.class))
            }),
            @ApiResponse(responseCode = "400", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
            }),
            @ApiResponse(responseCode = "500", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
            })
    })
    @PostMapping
    public ResponseEntity<GroupResponseDto> createGroup(@Valid @RequestBody GroupRequestDto groupRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(groupApplicationService.createGroup(groupRequestDto));
    }

    @SecurityRequirements
    @Observed(contextualName = "Tracing getGroupById method controller")
    @Operation(summary = "Получить информацию о группе.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = GroupResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
            }),
            @ApiResponse(responseCode = "500", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
            })
    })
    @GetMapping("/{id}")
    public ResponseEntity<GroupResponseDto> getGroupById(@Parameter(description = "ID группы.") @PathVariable UUID id) {
        return ResponseEntity.ok(groupApplicationService.getGroupById(id));
    }

    @PreAuthorize("hasAnyAuthority('users_viewer', 'users_admin')")
    @Observed(contextualName = "Tracing deleteGroupById method controller")
    @Operation(summary = "Удалить группу.")
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
    public ResponseEntity<Void> deleteGroupById(@Parameter(description = "ID группы.") @PathVariable UUID id) {
        groupApplicationService.deleteGroupById(id);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyAuthority('users_viewer', 'users_admin')")
    @Observed(contextualName = "Tracing updateGroupById method controller")
    @Operation(summary = "Обновить информацию о группе.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = GroupResponseDto.class))
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
    public ResponseEntity<GroupResponseDto> updateGroupById(@Parameter(description = "ID группы.") @PathVariable UUID id,
                                                            @Valid @RequestBody GroupRequestDto groupRequestDto) {

        return ResponseEntity.ok(groupApplicationService.updateGroupById(id, groupRequestDto));
    }
}
