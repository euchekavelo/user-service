package ru.tw1.euchekavelo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import ru.tw1.euchekavelo.model.enums.Sex;

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

    private String lastName;

    private String firstName;

    private String middleName;

    private LocalDate birthDate;

    private String email;

    private String phone;

    @Enumerated(EnumType.STRING)
    private Sex sex;

    @OneToMany(mappedBy = "sourceUser", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<UserSubscription> userSourceList = new ArrayList<>();

    @OneToMany(mappedBy = "destinationUser", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<UserSubscription> userDestinationList = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "photo_id", referencedColumnName = "id")
    @JsonBackReference
    private Photo photo;
}
