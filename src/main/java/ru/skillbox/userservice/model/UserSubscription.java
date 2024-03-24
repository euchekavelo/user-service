package ru.skillbox.userservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_subscriptions", schema = "users_scheme")
@Data
public class UserSubscription {

    @EmbeddedId
    private UserSubscriptionKey userSubscriptionKey;

    @ManyToOne
    @MapsId("sourceUserId")
    @JoinColumn(name = "source_user_id")
    @JsonBackReference
    private User sourceUser;

    @ManyToOne
    @MapsId("destinationUserId")
    @JoinColumn(name = "destination_user_id")
    @JsonBackReference
    private User destinationUser;

    @CreationTimestamp
    private LocalDateTime creationTime;

    @JsonIgnore
    private boolean deleted;
}
