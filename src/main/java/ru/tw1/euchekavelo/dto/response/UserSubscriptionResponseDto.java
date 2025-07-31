package ru.tw1.euchekavelo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class UserSubscriptionResponseDto {

    private UUID userId;
    private LocalDateTime creationTime;
}
