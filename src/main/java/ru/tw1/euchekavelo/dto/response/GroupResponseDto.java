package ru.tw1.euchekavelo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Schema(description = "Выходящий объект с информацией о группе.")
@Data
@AllArgsConstructor
public class GroupResponseDto {

    @Schema(description = "ID группы")
    private UUID id;

    @Schema(description = "Название группы")
    private String name;
}
