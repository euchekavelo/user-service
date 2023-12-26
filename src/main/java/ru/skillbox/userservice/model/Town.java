package ru.skillbox.userservice.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.*;

@Entity
@Table(name = "towns", schema = "users_scheme")
@SQLDelete(sql = "UPDATE users_scheme.towns SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
@Data
public class Town {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String name;

    @OneToMany(
            mappedBy = "town",
            cascade = CascadeType.MERGE
    )
    private List<User> userList = new ArrayList<>();

    private boolean deleted;
}
