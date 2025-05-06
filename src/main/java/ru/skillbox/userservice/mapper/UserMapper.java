package ru.skillbox.userservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.skillbox.userservice.dto.ShortUserDto;
import ru.skillbox.userservice.dto.response.UserResponseDto;
import ru.skillbox.userservice.dto.response.UserSubscriptionResponseDto;
import ru.skillbox.userservice.model.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UserMapper {

    @Autowired
    protected GroupMapper groupMapper;

    @Autowired
    protected TownMapper townMapper;

    @Autowired
    protected PhotoMapper photoMapper;

    public UserResponseDto userToUserResponseDto(User user) {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(user.getId());
        userResponseDto.setSex(user.getSex());
        userResponseDto.setPhone(user.getPhone());
        userResponseDto.setEmail(user.getEmail());
        userResponseDto.setFullname(user.getFullName());
        userResponseDto.setBirthDate(user.getBirthDate());
        userResponseDto.setGroups(groupMapper.userGroupListToGroupReponseDtoList(user.getUserGroupList()));
        userResponseDto.setTown(townMapper.townToTownResponseDto(user.getTown()));
        List<UserSubscriptionResponseDto> userSourceDtoList = user.getUserSourceList().stream()
                .map(userSubscription -> new UserSubscriptionResponseDto(userSubscription.getDestinationUser().getId(),
                        userSubscription.getCreationTime()))
                .toList();
        userResponseDto.setSubscriptions(userSourceDtoList);
        List<UserSubscriptionResponseDto> userDestinationDtoList = user.getUserDestinationList().stream()
                .map(userSubscription -> new UserSubscriptionResponseDto(userSubscription.getSourceUser().getId(),
                        userSubscription.getCreationTime()))
                .toList();
        userResponseDto.setSubscribers(userDestinationDtoList);
        userResponseDto.setPhoto(photoMapper.photoToUserPhotoResponseDto(user.getPhoto()));

        return userResponseDto;
    }

    public abstract User shortUserDtoToUser(ShortUserDto shortUserDto);
}
