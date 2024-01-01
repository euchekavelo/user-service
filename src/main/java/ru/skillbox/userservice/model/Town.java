package ru.skillbox.userservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
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

    @OneToMany(mappedBy = "town", cascade = CascadeType.MERGE)
    @JsonManagedReference
    private List<User> userList = new ArrayList<>();

    @JsonIgnore
    private boolean deleted;
}
