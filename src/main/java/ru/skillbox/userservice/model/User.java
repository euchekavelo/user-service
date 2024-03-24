package ru.skillbox.userservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import ru.skillbox.userservice.model.enums.Sex;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users", schema = "users_scheme")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String fullname;

    private LocalDate birthDate;

    private String email;

    private String phone;

    @ManyToOne
    @JoinColumn(name = "town_id")
    @JsonBackReference
    private Town town;

    @Enumerated(EnumType.STRING)
    private Sex sex;

    @OneToMany(mappedBy = "sourceUser", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<UserSubscription> userSourceList = new ArrayList<>();

    @OneToMany(mappedBy = "destinationUser", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<UserSubscription> userDestinationList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<UserGroup> userGroupList = new ArrayList<>();

    @JsonIgnore
    private boolean deleted;
}
