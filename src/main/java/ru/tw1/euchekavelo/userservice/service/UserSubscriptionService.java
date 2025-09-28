package ru.tw1.euchekavelo.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tw1.euchekavelo.userservice.exception.UserSubscriptionException;
import ru.tw1.euchekavelo.userservice.exception.UserSubscriptionNotFoundException;
import ru.tw1.euchekavelo.userservice.model.User;
import ru.tw1.euchekavelo.userservice.model.UserSubscription;
import ru.tw1.euchekavelo.userservice.model.UserSubscriptionKey;
import ru.tw1.euchekavelo.userservice.repository.UserSubscriptionRepository;

import java.util.Optional;
import java.util.UUID;

import static ru.tw1.euchekavelo.userservice.exception.enums.ExceptionMessage.*;
import static ru.tw1.euchekavelo.userservice.exception.enums.ExceptionMessage.USER_UNSUBSCRIBE_HIMSELF_EXCEPTION_MESSAGE;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSubscriptionService {

    private final UserSubscriptionRepository userSubscriptionRepository;
    private final UserService userService;
    private final AuthorizationService authorizationService;

    public void subscribeToUser(UUID sourceUserId, UUID destinationUserId) {
        authorizationService.checkAccess(sourceUserId);

        if (sourceUserId.equals(destinationUserId)) {
            log.error(USER_SUBSCRIPTION_EXCEPTION_MESSAGE.getExceptionMessage());
            throw new UserSubscriptionException(USER_SUBSCRIPTION_EXCEPTION_MESSAGE.getExceptionMessage());
        }

        User sourceUser = userService.getUserById(sourceUserId);
        User destinationUser = userService.getUserById(destinationUserId);

        UserSubscriptionKey userSubscriptionKey = getUserSubscriptionKey(sourceUserId, destinationUserId);

        Optional<UserSubscription> optionalUserSubscription = userSubscriptionRepository.findById(userSubscriptionKey);
        if (optionalUserSubscription.isPresent()) {
            log.error(USER_SUBSCRIPTION_ALREADY_EXISTS_EXCEPTION_MESSAGE.getExceptionMessage());
            throw new UserSubscriptionException(USER_SUBSCRIPTION_ALREADY_EXISTS_EXCEPTION_MESSAGE.getExceptionMessage());
        }

        UserSubscription userSubscription = new UserSubscription();
        userSubscription.setUserSubscriptionKey(userSubscriptionKey);
        userSubscription.setSourceUser(sourceUser);
        userSubscription.setDestinationUser(destinationUser);

        userSubscriptionRepository.save(userSubscription);
    }

    public void unsubscribeFromUser(UUID sourceUserId, UUID destinationUserId) {
        authorizationService.checkAccess(sourceUserId);

        if (sourceUserId.equals(destinationUserId)) {
            log.error(USER_UNSUBSCRIBE_HIMSELF_EXCEPTION_MESSAGE.getExceptionMessage());
            throw new UserSubscriptionException(USER_UNSUBSCRIBE_HIMSELF_EXCEPTION_MESSAGE.getExceptionMessage());
        }

        UserSubscriptionKey userSubscriptionKey = getUserSubscriptionKey(sourceUserId, destinationUserId);

        UserSubscription userSubscription = userSubscriptionRepository.findById(userSubscriptionKey)
                .orElseThrow(() -> {
                    log.error(USER_SUBSCRIPTION_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
                    return new UserSubscriptionNotFoundException(USER_SUBSCRIPTION_NOT_FOUND_EXCEPTION_MESSAGE
                            .getExceptionMessage());
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
