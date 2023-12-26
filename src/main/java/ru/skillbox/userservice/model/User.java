package ru.skillbox.userservice.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import ru.skillbox.userservice.model.enums.Sex;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users", schema = "users_scheme")
@SQLDelete(sql = "UPDATE users_scheme.users SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
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
    private Town town;

    @Enumerated(EnumType.STRING)
    private Sex sex;

    @OneToMany(mappedBy = "sourceUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserSubscription> userSubscriptionList = new ArrayList<>();

    @OneToMany(mappedBy = "destinationUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserSubscription> userDestinationList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<UserGroup> userGroupList = new ArrayList<>();

    private boolean deleted;
}
