package ru.tw1.euchekavelo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users_groups", schema = "users_scheme")
@Data
public class UserGroup {

    @EmbeddedId
    private UserGroupKey userGroupKey;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @ManyToOne
    @MapsId("groupId")
    @JoinColumn(name = "group_id")
    @JsonBackReference
    private Group group;
}
