package ru.tw1.euchekavelo.userservice.dto.external.response;

import lombok.Data;

@Data
public class AccessResponseDto {

    private boolean manageGroupMembership;
    private boolean view;
    private boolean mapRoles;
    private boolean impersonate;
    private boolean manage;
}
