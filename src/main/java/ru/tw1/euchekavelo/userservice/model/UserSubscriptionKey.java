package ru.tw1.euchekavelo.userservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Data
public class UserSubscriptionKey implements Serializable {

    @Column(name = "source_user_id")
    private UUID sourceUserId;

    @Column(name = "destination_user_id")
    private UUID destinationUserId;
}
