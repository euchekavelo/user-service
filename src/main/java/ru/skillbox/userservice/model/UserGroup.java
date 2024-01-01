package ru.skillbox.userservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "users_groups", schema = "users_scheme")
@SQLDelete(sql = "UPDATE users_scheme.users_groups SET deleted = true WHERE group_id=? OR user_id=?")
@Where(clause = "deleted=false")
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

    @JsonIgnore
    private boolean deleted;
}
