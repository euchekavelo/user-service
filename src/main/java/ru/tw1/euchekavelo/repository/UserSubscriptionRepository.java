package ru.tw1.euchekavelo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tw1.euchekavelo.model.UserSubscription;
import ru.tw1.euchekavelo.model.UserSubscriptionKey;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, UserSubscriptionKey> {
}
