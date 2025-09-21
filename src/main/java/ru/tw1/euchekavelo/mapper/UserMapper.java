package ru.tw1.euchekavelo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.tw1.euchekavelo.dto.external.request.AttributesRequestDto;
import ru.tw1.euchekavelo.dto.external.request.CredentialsRequestDto;
import ru.tw1.euchekavelo.dto.external.request.UserRepresentationRequestDto;
import ru.tw1.euchekavelo.dto.external.response.UserExternalResponseDto;
import ru.tw1.euchekavelo.dto.request.ShortUserRequestDto;
import ru.tw1.euchekavelo.dto.request.UserRequestDto;
import ru.tw1.euchekavelo.dto.response.UserResponseDto;
import ru.tw1.euchekavelo.dto.response.UserSubscriptionResponseDto;
import ru.tw1.euchekavelo.model.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UserMapper {

    @Autowired
    protected PhotoMapper photoMapper;

    public UserResponseDto userToUserResponseDto(User user) {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(user.getId());
        userResponseDto.setSex(user.getSex());
        userResponseDto.setPhone(user.getPhone());
        userResponseDto.setEmail(user.getEmail());
        userResponseDto.setLastName(user.getLastName());
        userResponseDto.setFirstName(user.getFirstName());
        userResponseDto.setMiddleName(user.getMiddleName());
        userResponseDto.setBirthDate(user.getBirthDate());
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

    public abstract User shortUserDtoToUser(ShortUserRequestDto shortUserRequestDto);

    public UserRepresentationRequestDto userToUserRepresentationRequestDto(User user) {
        UserRepresentationRequestDto userRepresentationRequestDto = new UserRepresentationRequestDto();
        userRepresentationRequestDto.setFirstName(user.getFirstName());
        userRepresentationRequestDto.setLastName(user.getLastName());
        userRepresentationRequestDto.setEmail(user.getEmail());
        userRepresentationRequestDto.setUsername(user.getEmail());
        userRepresentationRequestDto.setEnabled(true);
        userRepresentationRequestDto.setEmailVerified(true);

        CredentialsRequestDto credentialsRequestDto = new CredentialsRequestDto();
        credentialsRequestDto.setTemporary(false);
        credentialsRequestDto.setType("password");
        userRepresentationRequestDto.setCredentials(List.of(credentialsRequestDto));

        AttributesRequestDto attributesRequestDto = new AttributesRequestDto();
        attributesRequestDto.setUserId(List.of(user.getId().toString()));
        userRepresentationRequestDto.setAttributes(attributesRequestDto);

        return userRepresentationRequestDto;
    }

    public abstract User userRequestDtoToUser(UserRequestDto userRequestDto);

    public abstract User userToUser(@MappingTarget User toUser, User fromUser);

    public abstract User userExternalResponseDtoToUser(UserExternalResponseDto userExternalResponseDto);
}
