package ru.skillbox.userservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Data
public class UserPhotoResponseDto {

    @Schema(defaultValue = "3fa33f33-3333-3333-b2fc-3c333f33afa3", description = "Идентификатор фотографии пользователя.")
    private UUID id;

    @Schema(defaultValue = "Ссылка на фотографию", description = "Ссылка на фотографию пользователя.")
    private String link;

    @Schema(defaultValue = "Имя файла фотографии пользователя", description = "Имя файла фотографии пользователя.")
    private String name;
}
