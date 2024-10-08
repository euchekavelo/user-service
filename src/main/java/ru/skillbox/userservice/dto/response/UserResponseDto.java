package ru.skillbox.userservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.skillbox.userservice.model.enums.Sex;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Schema(description = "Выходящий объект с информацией о сотруднике.")
@Data
public class UserResponseDto {

    @Schema(description = "ID сотрудника")
    private UUID id;

    @Schema(description = "ФИО сотрудника")
    private String fullname;

    @Schema(description = "Дата рождения")
    private LocalDate birthDate;

    @Schema(description = "Почта")
    private String email;

    @Schema(description = "Телефон")
    private String phone;

    @Schema(description = "Информация о городе")
    private TownResponseDto town;

    @Schema(description = "Пол")
    private Sex sex;

    @Schema(description = "Информация о подписках")
    private List<UserSubscriptionResponseDto> subscriptions;

    @Schema(description = "Информация о подписчиках")
    private List<UserSubscriptionResponseDto> subscribers;

    @Schema(description = "Информация о группах")
    private List<GroupResponseDto> groups;

    @Schema(description = "Информация о фото")
    private UserPhotoResponseDto photo;
}
