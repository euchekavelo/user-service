package ru.tw1.euchekavelo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "Входящий объект с информацией о пользователе (полный).")
@Data
public class UserRequestDto {

    @Schema(description = "Фамилия пользователя")
    @NotBlank
    private String lastName;

    @Schema(description = "Имя пользователя")
    @NotBlank
    private String firstName;

    @Schema(description = "Отчество пользователя")
    private String middleName;

    @Schema(description = "Почтовый адрес пользователя")
    @NotBlank
    private String email;

    @Schema(description = "Пол пользователя")
    @Pattern(regexp = "MALE|FEMALE")
    private String sex;

    @Schema(description = "дата рождения")
    private LocalDate birthDate;

    @Schema(description = "Телефон")
    private String phone;

    @Schema(description = "ID города")
    private UUID townId;
}
