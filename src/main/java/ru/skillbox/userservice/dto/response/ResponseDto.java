package ru.skillbox.userservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Schema(description = "Выходящий объект с общей информацией.")
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDto {

    @Schema(description = "ID объекта")
    private UUID id;

    @Schema(description = "Сообщение")
    private String message;

    @Schema(description = "Индикатор результата")
    private boolean result;
}
