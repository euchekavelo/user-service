package ru.skillbox.userservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class GroupResponseDto {

    private UUID id;
    private String name;
}
