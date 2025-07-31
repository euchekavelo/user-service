package ru.tw1.euchekavelo.dto.external.request;

import lombok.Data;

import java.util.List;

@Data
public class UserRepresentationRequestDto {

    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private boolean emailVerified;
    private boolean enabled;
    private List<CredentialsRequestDto> credentials;
    AttributesRequestDto attributes;
}
