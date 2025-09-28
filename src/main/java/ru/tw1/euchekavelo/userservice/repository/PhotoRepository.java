package ru.tw1.euchekavelo.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tw1.euchekavelo.userservice.model.Photo;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, UUID> {

    Optional<Photo> findPhotoByUserIdAndId(UUID userId, UUID id);
}
