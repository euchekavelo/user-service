package ru.tw1.euchekavelo.userservice.dto.external.request;

import lombok.Data;

@Data
public class CredentialsRequestDto {

    private boolean temporary;
    private String type;
    private String value;
}
