package ru.skillbox.userservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.skillbox.userservice.config.ConfigGroupServiceTest;
import ru.skillbox.userservice.dto.GroupDto;
import ru.skillbox.userservice.dto.ResponseDto;
import ru.skillbox.userservice.exception.GroupNotFoundException;
import ru.skillbox.userservice.model.Group;
import ru.skillbox.userservice.repository.GroupRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ConfigGroupServiceTest.class)
class GroupServiceTest {

    @Autowired
    private GroupService groupService;

    @Autowired
    private GroupRepository groupRepository;

    @Test
    void createGroupTestSuccess() {
        GroupDto groupDto = new GroupDto();
        groupDto.setName("Test group");
        ResponseDto responseDto = groupService.createGroup(groupDto);

        assertThat(responseDto.getMessage()).isEqualTo("The group has been successfully created.");
    }

    @Test
    void getGroupByIdTestSuccess() throws GroupNotFoundException {
        UUID groupId = UUID.fromString("15afa0c0-2fe3-47d9-916b-761e59b67caa");
        Group group = new Group();
        group.setName("Test group");
        group.setId(groupId);
        Mockito.when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        Group savedGroup = groupService.getGroupById(groupId);

        assertThat(savedGroup.getId()).isEqualTo(groupId);
        assertThat(savedGroup.getName()).isEqualTo("Test group");
    }

    @Test
    void getGroupByIdTestThrowGroupNotFoundException() {
        UUID groupId = UUID.fromString("15afa0c0-2fe3-47d9-916b-761e59b67caa");
        Mockito.when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

        assertThrows(GroupNotFoundException.class, () -> groupService.getGroupById(groupId));
    }

    @Test
    void deleteGroupByIdTestSuccess() throws GroupNotFoundException {
        UUID groupId = UUID.fromString("15afa0c0-2fe3-47d9-916b-761e59b67caa");
        Mockito.when(groupRepository.findById(groupId)).thenReturn(Optional.of(Mockito.mock(Group.class)));

        assertDoesNotThrow(() -> groupService.deleteGroupById(groupId));
        assertThat(groupService.deleteGroupById(groupId).getMessage())
                .isEqualTo("The group was successfully removed.");
    }

    @Test
    void deleteGroupByIdTestThrowGroupNotFoundException() {
        UUID groupId = UUID.fromString("15afa0c0-2fe3-47d9-916b-761e59b67caa");
        Mockito.when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

        assertThrows(GroupNotFoundException.class, () -> groupService.deleteGroupById(groupId));
    }

    @Test
    void updateGroupByIdTestSuccess() throws GroupNotFoundException {
        GroupDto groupDto = new GroupDto();
        groupDto.setName("Test group");

        UUID groupId = UUID.fromString("15afa0c0-2fe3-47d9-916b-761e59b67caa");
        Group group = new Group();
        group.setName("Test group");
        group.setId(groupId);
        Mockito.when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));

        assertThat(groupService.updateGroupById(groupId, groupDto).getMessage())
                .isEqualTo("The group with the specified ID was successfully updated.");
    }

    @Test
    void updateGroupByIdTestThrowGroupNotFoundException() {
        UUID groupId = UUID.fromString("15afa0c0-2fe3-47d9-916b-761e59b67caa");
        GroupDto groupDto = new GroupDto();
        groupDto.setName("Test group");
        Mockito.when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

        assertThrows(GroupNotFoundException.class, () -> groupService.updateGroupById(groupId, groupDto));
    }
}