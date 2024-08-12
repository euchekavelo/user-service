package ru.skillbox.userservice.service;

import ru.skillbox.userservice.dto.GroupDto;
import ru.skillbox.userservice.dto.response.GroupResponseDto;
import ru.skillbox.userservice.exception.GroupNotFoundException;

import java.util.UUID;

public interface GroupService {

    GroupResponseDto createGroup(GroupDto groupDto);

    GroupResponseDto getGroupById(UUID id) throws GroupNotFoundException;

    void deleteGroupById(UUID id) throws GroupNotFoundException;

    GroupResponseDto updateGroupById(UUID id, GroupDto groupDto) throws GroupNotFoundException;
}
