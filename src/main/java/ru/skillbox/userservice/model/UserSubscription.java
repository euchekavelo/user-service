package ru.skillbox.userservice.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_subscriptions", schema = "users_scheme")
@SQLDelete(sql = "UPDATE users_scheme.user_subscriptions SET deleted = true WHERE destination_user_id=? OR source_user_id=?")
@Where(clause = "deleted=false")
@Data
public class UserSubscription {

    @EmbeddedId
    private UserSubscriptionKey userSubscriptionKey;

    @ManyToOne
    @MapsId("sourceUserId")
    @JoinColumn(name = "source_user_id")
    private User sourceUser;

    @ManyToOne
    @MapsId("destinationUserId")
    @JoinColumn(name = "destination_user_id")
    private User destinationUser;

    @CreationTimestamp
    private LocalDateTime creationTime;

    private boolean deleted;
}
