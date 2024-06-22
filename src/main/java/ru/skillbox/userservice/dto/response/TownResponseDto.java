package ru.skillbox.userservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class TownResponseDto {

    private UUID id;
    private String name;
}
