package ru.skillbox.userservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.skillbox.userservice.dto.GroupDto;
import ru.skillbox.userservice.dto.ResponseDto;
import ru.skillbox.userservice.exception.GroupNotFoundException;
import ru.skillbox.userservice.model.Group;
import ru.skillbox.userservice.repository.GroupRepository;
import ru.skillbox.userservice.service.GroupService;

import java.util.Optional;
import java.util.UUID;

@Service
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;

    @Autowired
    public GroupServiceImpl(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Override
    public ResponseDto createGroup(GroupDto groupDto) {
        Group group = new Group();
        group.setName(groupDto.getName());
        groupRepository.save(group);

        return getResponseDto("The group has been successfully created.");
    }

    @Override
    public Group getGroupById(UUID id) throws GroupNotFoundException {
        Optional<Group> optionalGroup = groupRepository.findById(id);
        if (optionalGroup.isEmpty()) {
            throw new GroupNotFoundException("The group with the specified ID was not found.");
        }

        return optionalGroup.get();
    }

    @Override
    public ResponseDto deleteGroupById(UUID id) throws GroupNotFoundException {
        Optional<Group> optionalGroup = groupRepository.findById(id);
        if (optionalGroup.isEmpty()) {
            throw new GroupNotFoundException("The group with the specified ID was not found.");
        }

        groupRepository.delete(optionalGroup.get());

        return getResponseDto("The group was successfully removed.");
    }

    @Override
    public ResponseDto updateGroupById(UUID id, GroupDto groupDto) throws GroupNotFoundException {
        Optional<Group> optionalGroup = groupRepository.findById(id);
        if (optionalGroup.isEmpty()) {
            throw new GroupNotFoundException("The group with the specified ID was not found.");
        }

        Group group = optionalGroup.get();
        group.setName(group.getName());
        groupRepository.save(group);

        return getResponseDto("The group with the specified ID was successfully updated.");
    }

    private ResponseDto getResponseDto(String message) {
        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage(message);
        responseDto.setResult(true);

        return responseDto;
    }
}
