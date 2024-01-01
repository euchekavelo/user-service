package ru.skillbox.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class UserDto {

    @NotBlank
    private String fullname;

    @NotBlank
    private String email;

    @Pattern(regexp = "MALE|FEMALE")
    private String sex;

    private LocalDate birthDate;

    private String phone;

    @NotBlank
    private UUID townId;
}
