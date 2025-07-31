package ru.tw1.euchekavelo.service.domain;

import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.tw1.euchekavelo.exception.UserNotFoundException;
import ru.tw1.euchekavelo.model.User;
import ru.tw1.euchekavelo.repository.UserRepository;

import java.util.UUID;

import static ru.tw1.euchekavelo.exception.enums.ExceptionMessage.USER_NOT_FOUND_EXCEPTION_MESSAGE;

@Observed
@Service
@RequiredArgsConstructor
public class UserDomainService {

    private final UserRepository userRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserDomainService.class);

    public User findUserById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> {
            LOGGER.error(USER_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
            return new UserNotFoundException(USER_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
        });
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(User user) {
        return saveUser(user);
    }

    public void deleteUserById(UUID id) {
        User user = findUserById(id);
        userRepository.delete(user);
    }
}
