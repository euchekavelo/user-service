package ru.skillbox.userservice.dto.response;

import lombok.Data;
import ru.skillbox.userservice.model.enums.Sex;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class UserResponseDto {

    private UUID id;
    private String fullname;
    private LocalDate birthDate;
    private String email;
    private String phone;
    private TownResponseDto town;
    private Sex sex;
    private List<UserSubscriptionResponseDto> subscriptions;
    private List<UserSubscriptionResponseDto> subscribers;
    private List<GroupResponseDto> userGroupList;
}
