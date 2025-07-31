package ru.tw1.euchekavelo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import ru.tw1.euchekavelo.dto.request.GroupRequestDto;
import ru.tw1.euchekavelo.dto.response.GroupResponseDto;
import ru.tw1.euchekavelo.model.Group;
import ru.tw1.euchekavelo.model.UserGroup;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class GroupMapper {

    public abstract GroupResponseDto groupToGroupResponseDto(Group group);

    public abstract Group groupRequestDtoToGroup(GroupRequestDto groupRequestDto);

    public List<GroupResponseDto> userGroupListToGroupReponseDtoList(List<UserGroup> userGroupList) {
        return userGroupList.stream()
                .map(userGroup -> new GroupResponseDto(userGroup.getGroup().getId(), userGroup.getGroup().getName()))
                .toList();
    }

    public abstract Group groupRequestDtoToGroup(@MappingTarget Group group, GroupResponseDto groupResponseDto);
}
