package ru.tw1.euchekavelo.service.domain;

import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.tw1.euchekavelo.exception.UserSubscriptionException;
import ru.tw1.euchekavelo.model.User;
import ru.tw1.euchekavelo.model.UserSubscription;
import ru.tw1.euchekavelo.model.UserSubscriptionKey;
import ru.tw1.euchekavelo.repository.UserSubscriptionRepository;

import java.util.Optional;
import java.util.UUID;

import static ru.tw1.euchekavelo.exception.enums.ExceptionMessage.USER_SUBSCRIPTION_ALREADY_EXISTS_EXCEPTION_MESSAGE;
import static ru.tw1.euchekavelo.exception.enums.ExceptionMessage.USER_SUBSCRIPTION_NOT_FOUND_EXCEPTION_MESSAGE;

@Observed
@Service
@RequiredArgsConstructor
public class UserSubscriptionDomainService {

    private final UserSubscriptionRepository userSubscriptionRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserSubscriptionDomainService.class);

    public UserSubscription createUserSubscription(User sourceUser, User destinationUser) {
        UserSubscriptionKey userSubscriptionKey = getUserSubscriptionKey(sourceUser.getId(), destinationUser.getId());

        Optional<UserSubscription> optionalUserSubscription = userSubscriptionRepository.findById(userSubscriptionKey);
        if (optionalUserSubscription.isPresent()) {
            LOGGER.error(USER_SUBSCRIPTION_ALREADY_EXISTS_EXCEPTION_MESSAGE.getExceptionMessage());
            throw new UserSubscriptionException(USER_SUBSCRIPTION_ALREADY_EXISTS_EXCEPTION_MESSAGE.getExceptionMessage());
        }

        UserSubscription userSubscription = new UserSubscription();
        userSubscription.setUserSubscriptionKey(userSubscriptionKey);
        userSubscription.setSourceUser(sourceUser);
        userSubscription.setDestinationUser(destinationUser);

        return userSubscriptionRepository.save(userSubscription);
    }

    public void deleteUserSubscriptionBySourceUserIdAndDestinationUserId(UUID sourceUserId, UUID destinationUserId) {
        UserSubscriptionKey userSubscriptionKey = getUserSubscriptionKey(sourceUserId, destinationUserId);

        UserSubscription userSubscription = userSubscriptionRepository.findById(userSubscriptionKey)
            .orElseThrow(() -> {
                LOGGER.error(USER_SUBSCRIPTION_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
                return new UserSubscriptionException(USER_SUBSCRIPTION_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
            });

        userSubscriptionRepository.delete(userSubscription);
    }

    private UserSubscriptionKey getUserSubscriptionKey(UUID userSourceId, UUID destinationUserId) {
        UserSubscriptionKey userSubscriptionKey = new UserSubscriptionKey();
        userSubscriptionKey.setSourceUserId(userSourceId);
        userSubscriptionKey.setDestinationUserId(destinationUserId);

        return userSubscriptionKey;
    }
}
