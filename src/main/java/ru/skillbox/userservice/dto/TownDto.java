package ru.skillbox.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "Входящий объект с информацией о городе.")
@Data
public class TownDto {

    @Schema(description = "Название города")
    @NotBlank
    private String name;
}
