package ru.skillbox.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Schema(description = "Входящий объект с информацией о подписке.")
@Data
public class UserSubscriptionDto {

    @Schema(description = "ID пользователя (инициатор запроса)")
    private UUID sourceUserId;

    @Schema(description = "ID пользователя (приемник запроса)")
    private UUID destinationUserId;
}
