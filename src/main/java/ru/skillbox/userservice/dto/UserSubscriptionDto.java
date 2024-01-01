package ru.skillbox.userservice.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class UserSubscriptionDto {

    private UUID sourceUserId;
    private UUID destinationUserId;
}
