package ru.skillbox.userservice.service.impl;

import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.skillbox.userservice.dto.GroupDto;
import ru.skillbox.userservice.dto.response.GroupResponseDto;
import ru.skillbox.userservice.exception.GroupNotFoundException;
import ru.skillbox.userservice.mapper.GroupMapper;
import ru.skillbox.userservice.model.Group;
import ru.skillbox.userservice.repository.GroupRepository;
import ru.skillbox.userservice.service.GroupService;

import java.util.Optional;
import java.util.UUID;

import static ru.skillbox.userservice.exception.enums.ExceptionMessage.GROUP_NOT_FOUND_EXCEPTION_MESSAGE;

@Observed
@Service
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final GroupMapper groupMapper;
    private final Logger logger = LoggerFactory.getLogger(GroupServiceImpl.class);

    @Autowired
    public GroupServiceImpl(GroupRepository groupRepository, GroupMapper groupMapper) {
        this.groupRepository = groupRepository;
        this.groupMapper = groupMapper;
    }

    @Override
    public GroupResponseDto createGroup(GroupDto groupDto) {
        Group group = new Group();
        group.setName(groupDto.getName());
        Group savedGroup = groupRepository.save(group);

        return groupMapper.groupToGroupResponseDto(savedGroup);
    }

    @Override
    public GroupResponseDto getGroupById(UUID id) throws GroupNotFoundException {
        Optional<Group> optionalGroup = groupRepository.findById(id);
        if (optionalGroup.isEmpty()) {
            logger.error(GROUP_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
            throw new GroupNotFoundException(GROUP_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
        }

        return groupMapper.groupToGroupResponseDto(optionalGroup.get());
    }

    @Override
    public void deleteGroupById(UUID id) throws GroupNotFoundException {
        Optional<Group> optionalGroup = groupRepository.findById(id);
        if (optionalGroup.isEmpty()) {
            logger.error(GROUP_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
            throw new GroupNotFoundException(GROUP_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
        }

        groupRepository.delete(optionalGroup.get());
    }

    @Override
    public GroupResponseDto updateGroupById(UUID id, GroupDto groupDto) throws GroupNotFoundException {
        Optional<Group> optionalGroup = groupRepository.findById(id);
        if (optionalGroup.isEmpty()) {
            logger.error(GROUP_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
            throw new GroupNotFoundException(GROUP_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
        }

        Group group = optionalGroup.get();
        group.setName(group.getName());
        Group updatedGroup = groupRepository.save(group);

        return groupMapper.groupToGroupResponseDto(updatedGroup);
    }
}
