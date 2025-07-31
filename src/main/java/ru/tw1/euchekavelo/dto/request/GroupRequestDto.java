package ru.tw1.euchekavelo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "Входящий объект с информацией о группе.")
@Data
public class GroupRequestDto {

    @Schema(description = "Название группы")
    @NotBlank
    private String name;
}
