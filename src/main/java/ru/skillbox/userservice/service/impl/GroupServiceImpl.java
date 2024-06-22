package ru.skillbox.userservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.skillbox.userservice.dto.GroupDto;
import ru.skillbox.userservice.dto.response.ResponseDto;
import ru.skillbox.userservice.exception.GroupNotFoundException;
import ru.skillbox.userservice.model.Group;
import ru.skillbox.userservice.repository.GroupRepository;
import ru.skillbox.userservice.service.GroupService;

import java.util.Optional;
import java.util.UUID;

import static ru.skillbox.userservice.exception.enums.ExceptionMessage.GROUP_NOT_FOUND_EXCEPTION_MESSAGE;

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
        Group savedGroup = groupRepository.save(group);

        return getResponseDto("The group has been successfully created.", savedGroup.getId());
    }

    @Override
    public Group getGroupById(UUID id) throws GroupNotFoundException {
        Optional<Group> optionalGroup = groupRepository.findById(id);
        if (optionalGroup.isEmpty()) {
            throw new GroupNotFoundException(GROUP_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
        }

        return optionalGroup.get();
    }

    @Override
    public ResponseDto deleteGroupById(UUID id) throws GroupNotFoundException {
        Optional<Group> optionalGroup = groupRepository.findById(id);
        if (optionalGroup.isEmpty()) {
            throw new GroupNotFoundException(GROUP_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
        }

        groupRepository.delete(optionalGroup.get());

        return getResponseDto("The group was successfully removed.", null);
    }

    @Override
    public ResponseDto updateGroupById(UUID id, GroupDto groupDto) throws GroupNotFoundException {
        Optional<Group> optionalGroup = groupRepository.findById(id);
        if (optionalGroup.isEmpty()) {
            throw new GroupNotFoundException(GROUP_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
        }

        Group group = optionalGroup.get();
        group.setName(group.getName());
        groupRepository.save(group);

        return getResponseDto("The group with the specified ID was successfully updated.", null);
    }

    private ResponseDto getResponseDto(String message, UUID id) {
        return ResponseDto.builder()
                .message(message)
                .id(id)
                .result(true)
                .build();
    }
}
