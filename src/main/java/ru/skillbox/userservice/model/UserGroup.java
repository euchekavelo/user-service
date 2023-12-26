package ru.skillbox.userservice.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "users_groups", schema = "users_scheme")
@SQLDelete(sql = "UPDATE users_scheme.users_groups SET deleted = true WHERE group_id=? or user_id=?")
@Where(clause = "deleted=false")
@Data
public class UserGroup {

    @EmbeddedId
    private UserGroupKey userGroupKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("groupId")
    @JoinColumn(name = "group_id")
    private Group group;

    private boolean deleted;
}
