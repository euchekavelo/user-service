package ru.tw1.euchekavelo.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tw1.euchekavelo.userservice.model.UserSubscription;
import ru.tw1.euchekavelo.userservice.model.UserSubscriptionKey;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, UserSubscriptionKey> {
}
