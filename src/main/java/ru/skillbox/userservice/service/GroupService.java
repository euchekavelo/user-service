package ru.skillbox.userservice.service;

import ru.skillbox.userservice.dto.GroupDto;
import ru.skillbox.userservice.dto.ResponseDto;
import ru.skillbox.userservice.exception.GroupNotFoundException;
import ru.skillbox.userservice.model.Group;

import java.util.UUID;

public interface GroupService {

    ResponseDto createGroup(GroupDto groupDto);

    Group getGroupById(UUID id) throws GroupNotFoundException;

    ResponseDto deleteGroupById(UUID id) throws GroupNotFoundException;

    ResponseDto updateGroupById(UUID id, GroupDto groupDto) throws GroupNotFoundException;
}
