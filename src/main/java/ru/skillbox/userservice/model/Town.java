package ru.skillbox.userservice.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "towns", schema = "users_scheme")
@Data
public class Town {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String name;

    @OneToMany(mappedBy = "town", cascade = CascadeType.MERGE)
    @JsonManagedReference
    private List<User> userList = new ArrayList<>();
}
