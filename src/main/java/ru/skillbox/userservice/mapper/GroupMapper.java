package ru.skillbox.userservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.skillbox.userservice.dto.response.GroupResponseDto;
import ru.skillbox.userservice.model.UserGroup;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class GroupMapper {

    public List<GroupResponseDto> userGroupListToGroupReponseDtoList(List<UserGroup> userGroupList) {
        return userGroupList.stream()
                .map(userGroup -> new GroupResponseDto(userGroup.getGroup().getId(), userGroup.getGroup().getName()))
                .toList();
    }
}
