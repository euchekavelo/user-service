package ru.skillbox.userservice.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.userservice.dto.GroupDto;
import ru.skillbox.userservice.dto.ResponseDto;
import ru.skillbox.userservice.exception.GroupNotFoundException;
import ru.skillbox.userservice.model.Group;
import ru.skillbox.userservice.service.GroupService;

import java.util.UUID;

@RestController
@RequestMapping("/groups")
public class GroupController {

    private final GroupService groupService;

    @Autowired
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping
    public ResponseEntity<ResponseDto> createGroup(@Valid @RequestBody GroupDto groupDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(groupService.createGroup(groupDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Group> getGroupById(@PathVariable UUID id) throws GroupNotFoundException {
        return ResponseEntity.ok(groupService.getGroupById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto> deleteGroupById(@PathVariable UUID id) throws GroupNotFoundException {
        return ResponseEntity.ok(groupService.deleteGroupById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto> updateGroupById(@PathVariable UUID id, @Valid @RequestBody GroupDto groupDto)
            throws GroupNotFoundException {

        return ResponseEntity.ok(groupService.updateGroupById(id, groupDto));
    }
}
