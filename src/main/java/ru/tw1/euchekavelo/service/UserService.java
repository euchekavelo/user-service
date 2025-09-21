package ru.tw1.euchekavelo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tw1.euchekavelo.service.api.ApiAuthService;
import ru.tw1.euchekavelo.exception.UserNotFoundException;
import ru.tw1.euchekavelo.mapper.UserMapper;
import ru.tw1.euchekavelo.model.User;
import ru.tw1.euchekavelo.repository.UserRepository;

import java.util.UUID;

import static ru.tw1.euchekavelo.exception.enums.ExceptionMessage.USER_NOT_FOUND_EXCEPTION_MESSAGE;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ApiAuthService apiAuthService;
    private final AuthorizationService authorizationService;

    @Transactional(rollbackFor = Throwable.class)
    public User createUser(User user) {
        String password = user.getPassword();
        user.setPassword(null);
        User savedUser = userRepository.save(user);

        User userToApi = userMapper.userToUser(new User(), savedUser);
        userToApi.setPassword(password);
        apiAuthService.registerUser(userToApi);

        return savedUser;
    }


    public User getUserById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> {
            log.error(USER_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
            return new UserNotFoundException(USER_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
        });
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Transactional(rollbackFor = Throwable.class)
    public User updateUser(UUID id, User user) {
        authorizationService.checkAccess(id);

        User existingUser = getUserById(id);
        UUID externalUserId = apiAuthService.getUserById(id).getId();

        String password = user.getPassword();
        user.setPassword(null);
        User updatedUserWithFields = userMapper.userToUser(existingUser, user);
        User updatedUser = userRepository.save(updatedUserWithFields);

        user.setPassword(password);
        apiAuthService.updateUserById(externalUserId, user);

        return updatedUser;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void deleteUserById(UUID id) {
        authorizationService.checkAccess(id);

        User user = getUserById(id);
        userRepository.delete(user);

        UUID externalUserId = apiAuthService.getUserById(id).getId();
        apiAuthService.deleteUserById(externalUserId);
    }
}
