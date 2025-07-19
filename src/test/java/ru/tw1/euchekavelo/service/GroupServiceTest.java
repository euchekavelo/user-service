package ru.tw1.euchekavelo.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.tw1.euchekavelo.config.ConfigGroupService;
import ru.tw1.euchekavelo.dto.request.GroupRequestDto;
import ru.tw1.euchekavelo.dto.response.GroupResponseDto;
import ru.tw1.euchekavelo.exception.GroupNotFoundException;
import ru.tw1.euchekavelo.model.Group;
import ru.tw1.euchekavelo.repository.GroupRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ConfigGroupService.class)
class GroupServiceTest {

    @Autowired
    private GroupService groupService;

    @Autowired
    private GroupRepository groupRepository;

    @Test
    void createGroupTestSuccess() {
        Group savedGroup = new Group();
        savedGroup.setId(UUID.fromString("15afa0c0-2fe3-47d9-916b-761e59b67caa"));
        savedGroup.setName("Test group");

        Mockito.when(groupRepository.save(Mockito.any(Group.class))).thenReturn(savedGroup);
        GroupRequestDto groupRequestDto = new GroupRequestDto();
        groupRequestDto.setName("Test group");
        GroupResponseDto groupResponseDto = groupService.createGroup(groupRequestDto);

        assertThat(groupResponseDto.getName()).isNotBlank();
    }

    @Test
    void getGroupByIdTestSuccess() throws GroupNotFoundException {
        UUID groupId = UUID.fromString("15afa0c0-2fe3-47d9-916b-761e59b67caa");
        Group group = new Group();
        group.setName("Test group");
        group.setId(groupId);
        Mockito.when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        GroupResponseDto groupResponseDto = groupService.getGroupById(groupId);

        assertThat(groupResponseDto.getId()).isEqualTo(groupId);
        assertThat(groupResponseDto.getName()).isEqualTo("Test group");
    }

    @Test
    void getGroupByIdTestThrowGroupNotFoundException() {
        UUID groupId = UUID.fromString("15afa0c0-2fe3-47d9-916b-761e59b67caa");
        Mockito.when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

        assertThrows(GroupNotFoundException.class, () -> groupService.getGroupById(groupId));
    }

    @Test
    void deleteGroupByIdTestSuccess() {
        UUID groupId = UUID.fromString("15afa0c0-2fe3-47d9-916b-761e59b67caa");
        Mockito.when(groupRepository.findById(groupId)).thenReturn(Optional.of(Mockito.mock(Group.class)));

        assertDoesNotThrow(() -> groupService.deleteGroupById(groupId));
    }

    @Test
    void deleteGroupByIdTestThrowGroupNotFoundException() {
        UUID groupId = UUID.fromString("15afa0c0-2fe3-47d9-916b-761e59b67caa");
        Mockito.when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

        assertThrows(GroupNotFoundException.class, () -> groupService.deleteGroupById(groupId));
    }

    @Test
    void updateGroupByIdTestSuccess() throws GroupNotFoundException {
        GroupRequestDto groupRequestDto = new GroupRequestDto();
        groupRequestDto.setName("Test group");

        UUID groupId = UUID.fromString("15afa0c0-2fe3-47d9-916b-761e59b67caa");
        Group group = new Group();
        group.setName("Test group");
        group.setId(groupId);
        Mockito.when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        Mockito.when(groupRepository.save(Mockito.any())).thenReturn(group);

        assertThat(groupService.updateGroupById(groupId, groupRequestDto).getName()).isNotBlank();
    }

    @Test
    void updateGroupByIdTestThrowGroupNotFoundException() {
        UUID groupId = UUID.fromString("15afa0c0-2fe3-47d9-916b-761e59b67caa");
        GroupRequestDto groupRequestDto = new GroupRequestDto();
        groupRequestDto.setName("Test group");
        Mockito.when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

        assertThrows(GroupNotFoundException.class, () -> groupService.updateGroupById(groupId, groupRequestDto));
    }
}