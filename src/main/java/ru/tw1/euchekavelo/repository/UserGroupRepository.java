package ru.tw1.euchekavelo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tw1.euchekavelo.model.UserGroup;
import ru.tw1.euchekavelo.model.UserGroupKey;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, UserGroupKey> {
}
