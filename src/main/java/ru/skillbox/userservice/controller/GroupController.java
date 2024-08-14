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
import ru.skillbox.userservice.dto.GroupDto;
import ru.skillbox.userservice.dto.response.GroupResponseDto;
import ru.skillbox.userservice.dto.response.ResponseDto;
import ru.skillbox.userservice.exception.GroupNotFoundException;
import ru.skillbox.userservice.service.GroupService;

import java.util.UUID;

@Tag(name="Контроллер по работе с группами", description="Спецификация API микросервиса по работе с группами.")
@RestController
@RequestMapping("/groups")
public class GroupController {

    private final GroupService groupService;

    @Autowired
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

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
    public ResponseEntity<GroupResponseDto> createGroup(@Valid @RequestBody GroupDto groupDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(groupService.createGroup(groupDto));
    }

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
    public ResponseEntity<GroupResponseDto> getGroupById(@Parameter(description = "ID группы.")
                                                         @PathVariable UUID id) throws GroupNotFoundException {
        return ResponseEntity.ok(groupService.getGroupById(id));
    }

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
    public ResponseEntity<Void> deleteGroupById(@Parameter(description = "ID группы.") @PathVariable UUID id)
            throws GroupNotFoundException {

        groupService.deleteGroupById(id);

        return ResponseEntity.noContent().build();
    }

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
                                                            @Valid @RequestBody GroupDto groupDto)
            throws GroupNotFoundException {

        return ResponseEntity.ok(groupService.updateGroupById(id, groupDto));
    }
}
