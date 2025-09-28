package ru.tw1.euchekavelo.userservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.tw1.euchekavelo.userservice.model.enums.Sex;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Schema(description = "Выходящий объект с информацией о сотруднике.")
@Data
public class UserResponseDto {

    @Schema(description = "ID сотрудника")
    private UUID id;

    @Schema(description = "Фамилия сотрудника")
    private String lastName;

    @Schema(description = "Имя сотрудника")
    private String firstName;

    @Schema(description = "Отчество сотрудника")
    private String middleName;

    @Schema(description = "Дата рождения")
    private LocalDate birthDate;

    @Schema(description = "Почта")
    private String email;

    @Schema(description = "Телефон")
    private String phone;

    @Schema(description = "Пол")
    private Sex sex;

    @Schema(description = "Информация о подписках")
    private List<UserSubscriptionResponseDto> subscriptions;

    @Schema(description = "Информация о подписчиках")
    private List<UserSubscriptionResponseDto> subscribers;

    @Schema(description = "Информация о фото")
    private UserPhotoResponseDto photo;
}
