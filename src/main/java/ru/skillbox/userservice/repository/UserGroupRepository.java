package ru.skillbox.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.userservice.model.UserGroup;
import ru.skillbox.userservice.model.UserGroupKey;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, UserGroupKey> {
}
