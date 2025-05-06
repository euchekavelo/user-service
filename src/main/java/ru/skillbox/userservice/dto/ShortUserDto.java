package ru.skillbox.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Schema(description = "Входящий объект с информацией о пользователе (сокращенный).")
@Data
public class ShortUserDto {

    @Schema(description = "Полное имя пользователя")
    @NotBlank
    private String fullName;

    @Schema(description = "Почтовый адрес пользователя")
    @NotBlank
    private String email;

    @Schema(description = "Пол пользователя")
    @Pattern(regexp = "MALE|FEMALE")
    private String sex;
}
