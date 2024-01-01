package ru.skillbox.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ShortUserDto {

    @NotBlank
    private String fullname;

    @NotBlank
    private String email;

    @Pattern(regexp = "MALE|FEMALE")
    private String sex;
}
