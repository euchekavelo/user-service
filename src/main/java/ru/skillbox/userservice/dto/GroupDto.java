package ru.skillbox.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GroupDto {

    @NotBlank
    private String name;
}
