package ru.tw1.euchekavelo.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.tw1.euchekavelo.config.ConfigGroupApplicationService;
import ru.tw1.euchekavelo.dto.request.GroupRequestDto;
import ru.tw1.euchekavelo.dto.response.GroupResponseDto;
import ru.tw1.euchekavelo.exception.GroupNotFoundException;
import ru.tw1.euchekavelo.exception.ResourceAccessDeniedException;
import ru.tw1.euchekavelo.model.Group;
import ru.tw1.euchekavelo.model.User;
import ru.tw1.euchekavelo.model.enums.Sex;
import ru.tw1.euchekavelo.security.util.UserDetailsContextUtil;
import ru.tw1.euchekavelo.service.application.GroupApplicationService;
import ru.tw1.euchekavelo.service.domain.GroupDomainService;
import ru.tw1.euchekavelo.service.domain.UserDomainService;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ConfigGroupApplicationService.class)
class GroupApplicationServiceTest {

    @Autowired
    private UserDetailsContextUtil userDetailsContextUtil;

    @Autowired
    private UserDomainService userDomainService;

    @Autowired
    private GroupDomainService groupDomainService;

    @Autowired
    private EntityAccessCheckService<Group> groupAccessCheckService;

    @Autowired
    private GroupApplicationService groupApplicationService;

    @Test
    void createGroupTestSuccess() {
        UUID userId = UUID.fromString("15afa0c0-2fe3-47d9-916b-761e59b67caa");
        Group savedGroup = getGroup();
        savedGroup.setId(UUID.fromString("17afa0c0-2fe3-47d9-916b-761e59b67caa"));

        Mockito.when(userDetailsContextUtil.getUserId()).thenReturn(userId);
        Mockito.when(userDomainService.findUserById(userId)).thenReturn(getSavedUser());
        Mockito.when(groupDomainService.createGroup(Mockito.any(Group.class))).thenReturn(savedGroup);
        GroupRequestDto groupRequestDto = getGroupRequestDto();
        GroupResponseDto groupResponseDto = groupApplicationService.createGroup(groupRequestDto);

        assertThat(groupResponseDto.getName()).isNotBlank();
    }

    @Test
    void getGroupByIdTestSuccess() throws GroupNotFoundException {
        UUID groupId = UUID.fromString("15afa0c0-2fe3-47d9-916b-761e59b67caa");
        Group group = getGroup();
        group.setId(groupId);
        Mockito.when(groupDomainService.getGroupById(groupId)).thenReturn(group);
        GroupResponseDto groupResponseDto = groupApplicationService.getGroupById(groupId);

        assertThat(groupResponseDto.getId()).isEqualTo(groupId);
        assertThat(groupResponseDto.getName()).isEqualTo("Test group");
    }

    @Test
    void getGroupByIdTestThrowGroupNotFoundException() {
        UUID groupId = UUID.fromString("15afa0c0-2fe3-47d9-916b-761e59b67caa");
        Mockito.when(groupDomainService.getGroupById(groupId)).thenThrow(GroupNotFoundException.class);

        assertThrows(GroupNotFoundException.class, () -> groupApplicationService.getGroupById(groupId));
    }

   @Test
    void deleteGroupByIdTestSuccess() {
        UUID groupId = UUID.fromString("15afa0c0-2fe3-47d9-916b-761e59b67caa");
        Mockito.when(groupDomainService.getGroupById(groupId)).thenReturn(Mockito.any(Group.class));

        assertDoesNotThrow(() -> groupApplicationService.deleteGroupById(groupId));
    }

    @Test
    void deleteGroupByIdTestThrowGroupNotFoundException() {
        UUID groupId = UUID.fromString("15afa0c0-2fe3-47d9-916b-161e59b67caa");
        Mockito.when(groupDomainService.getGroupById(groupId)).thenThrow(GroupNotFoundException.class);

        assertThrows(GroupNotFoundException.class, () -> groupApplicationService.deleteGroupById(groupId));
    }

    @Test
    void deleteGroupTestThrowResourceAccessDeniedException() {
        UUID groupId = UUID.fromString("12afa0c0-2fe3-47d9-916b-761e59b67caa");
        Group group = getGroup();
        group.setId(groupId);
        Mockito.when(groupDomainService.getGroupById(groupId)).thenReturn(group);

        Mockito.doThrow(ResourceAccessDeniedException.class).when(groupAccessCheckService)
                .checkEntityAccess(Mockito.any());

        assertThrows(ResourceAccessDeniedException.class, () -> groupApplicationService.deleteGroupById(groupId));
    }

    @Test
    void updateGroupByIdTestSuccess() throws GroupNotFoundException {
        GroupRequestDto groupRequestDto = getGroupRequestDto();

        UUID groupId = UUID.fromString("10afa0c0-2fe3-47d9-916b-761e59b67caa");
        Group group = getGroup();
        group.setId(groupId);

        Mockito.when(groupDomainService.getGroupById(groupId)).thenReturn(group);
        Mockito.doNothing().when(groupAccessCheckService).checkEntityAccess(Mockito.any(Group.class));
        Mockito.when(groupDomainService.updateGroup(Mockito.any(Group.class))).thenReturn(group);

        GroupResponseDto groupResponseDto = groupApplicationService.updateGroupById(groupId, groupRequestDto);
        assertThat(groupResponseDto.getName()).isNotBlank();
    }

    @Test
    void updateGroupByIdTestThrowGroupNotFoundException() {
        UUID groupId = UUID.fromString("15afa0c0-2fe3-47d9-116b-761e59b88caa");
        GroupRequestDto groupRequestDto = getGroupRequestDto();
        Mockito.when(groupDomainService.updateGroup(Mockito.any())).thenThrow(GroupNotFoundException.class);

        assertThrows(GroupNotFoundException.class, () -> groupApplicationService.updateGroupById(groupId, groupRequestDto));
    }

    @Test
    void updateGroupTestThrowAccessDeniedException() {
        UUID groupId = UUID.fromString("12afa0c0-2fe3-47d9-916b-761e59b98caa");
        Group group = getGroup();
        group.setId(groupId);
        GroupRequestDto groupRequestDto = getGroupRequestDto();
        Mockito.when(groupDomainService.getGroupById(groupId)).thenReturn(group);

        Mockito.doThrow(ResourceAccessDeniedException.class).when(groupAccessCheckService)
                .checkEntityAccess(Mockito.any());

        assertThrows(ResourceAccessDeniedException.class,
                () -> groupApplicationService.updateGroupById(groupId, groupRequestDto));
    }

    private Group getGroup() {
        Group group = new Group();
        group.setName("Test group");

        return group;
    }

    private GroupRequestDto getGroupRequestDto() {
        GroupRequestDto groupRequestDto = new GroupRequestDto();
        groupRequestDto.setName("Test group");

        return groupRequestDto;
    }

    private User getSavedUser() {
        User savedUser = new User();
        savedUser.setId(UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd"));
        savedUser.setLastName("Ivanov");
        savedUser.setFirstName("Ivan");
        savedUser.setMiddleName("Ivanovich");
        savedUser.setEmail("invanov_test@gmail.com");
        savedUser.setSex(Sex.valueOf("MALE"));

        return savedUser;
    }
}