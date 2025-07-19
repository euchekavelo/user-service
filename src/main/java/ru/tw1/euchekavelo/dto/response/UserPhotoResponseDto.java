package ru.tw1.euchekavelo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Schema(description = "Выходящий объект с информацией о фото.")
@Data
public class UserPhotoResponseDto {

    @Schema(defaultValue = "3fa33f33-3333-3333-b2fc-3c333f33afa3", description = "Идентификатор фотографии пользователя.")
    private UUID id;

    @Schema(defaultValue = "example.com/мое_фото.png", description = "Ссылка на фотографию пользователя.")
    private String link;

    @Schema(defaultValue = "мое_фото.png", description = "Имя файла фотографии пользователя.")
    private String name;
}
