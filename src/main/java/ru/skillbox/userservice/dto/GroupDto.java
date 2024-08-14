package ru.skillbox.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "Входящий объект с информацией о группе.")
@Data
public class GroupDto {

    @Schema(description = "Название группы")
    @NotBlank
    private String name;
}
