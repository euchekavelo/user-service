package ru.tw1.euchekavelo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Schema(description = "Выходящий объект с информацией о городе.")
@Data
@AllArgsConstructor
public class TownResponseDto {

    @Schema(description = "ID города")
    private UUID id;

    @Schema(description = "Название города")
    private String name;
}
