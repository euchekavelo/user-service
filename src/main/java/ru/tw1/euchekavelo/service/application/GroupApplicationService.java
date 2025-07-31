package ru.tw1.euchekavelo.service.application;

import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tw1.euchekavelo.dto.request.GroupRequestDto;
import ru.tw1.euchekavelo.dto.response.GroupResponseDto;
import ru.tw1.euchekavelo.mapper.GroupMapper;
import ru.tw1.euchekavelo.model.Group;
import ru.tw1.euchekavelo.model.User;
import ru.tw1.euchekavelo.security.util.UserDetailsContextUtil;
import ru.tw1.euchekavelo.service.EntityAccessCheckService;
import ru.tw1.euchekavelo.service.domain.GroupDomainService;
import ru.tw1.euchekavelo.service.domain.UserDomainService;
import java.util.UUID;

@Observed
@Service
@RequiredArgsConstructor
public class GroupApplicationService {

    private final UserDetailsContextUtil userDetailsContextUtil;
    private final UserDomainService userDomainService;
    private final GroupDomainService groupDomainService;
    private final GroupMapper groupMapper;
    private final EntityAccessCheckService<Group> groupAccessCheckService;

    public GroupResponseDto createGroup(GroupRequestDto groupRequestDto) {
        UUID currentUserId = userDetailsContextUtil.getUserId();
        User currentUser = userDomainService.findUserById(currentUserId);

        Group group = groupMapper.groupRequestDtoToGroup(groupRequestDto);
        group.setOwnerUser(currentUser);
        Group savedGroup = groupDomainService.createGroup(group);

        return groupMapper.groupToGroupResponseDto(savedGroup);
    }

    public GroupResponseDto getGroupById(UUID id) {
        return groupMapper.groupToGroupResponseDto(groupDomainService.getGroupById(id));
    }

    public void deleteGroupById(UUID id) {
        Group group = groupDomainService.getGroupById(id);
        groupAccessCheckService.checkEntityAccess(group);
        groupDomainService.deleteGroupById(id);
    }

    public GroupResponseDto updateGroupById(UUID id, GroupRequestDto groupRequestDto) {
        Group group = groupDomainService.getGroupById(id);
        groupAccessCheckService.checkEntityAccess(group);
        Group updatedGroup = groupMapper.groupRequestDtoToGroup(groupRequestDto);

        return groupMapper.groupToGroupResponseDto(groupDomainService.updateGroup(updatedGroup));
    }
}
