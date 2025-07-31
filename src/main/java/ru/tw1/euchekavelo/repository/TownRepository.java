package ru.tw1.euchekavelo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tw1.euchekavelo.model.Town;

import java.util.UUID;

@Repository
public interface TownRepository extends JpaRepository<Town, UUID> {
}
