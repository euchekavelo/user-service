package ru.tw1.euchekavelo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Schema(description = "Входящий объект с информацией о пользователе (сокращенный).")
@Data
public class ShortUserRequestDto {

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

    @Schema(description = "Пароль пользователя")
    @NotBlank
    private String password;

    @Schema(description = "Пол пользователя")
    @Pattern(regexp = "MALE|FEMALE")
    private String sex;
}
