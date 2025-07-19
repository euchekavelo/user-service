package ru.tw1.euchekavelo.dto.external.response;

import lombok.Data;

import java.util.UUID;

@Data
public class UserExternalResponseDto {

    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private boolean emailVerified;
    private long createdTimestamp;
    private boolean enabled;
    private AccessResponseDto access;
}
