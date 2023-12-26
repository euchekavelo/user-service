package ru.skillbox.userservice.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "groups", schema = "users_scheme")
@SQLDelete(sql = "UPDATE users_scheme.groups SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
@Data
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String name;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserGroup> userGroupList = new ArrayList<>();

    private boolean deleted;
}
