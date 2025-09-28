package ru.tw1.euchekavelo.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tw1.euchekavelo.userservice.adapter.api.AuthServiceApiAdapter;
import ru.tw1.euchekavelo.userservice.exception.UserNotFoundException;
import ru.tw1.euchekavelo.userservice.mapper.UserMapper;
import ru.tw1.euchekavelo.userservice.model.User;
import ru.tw1.euchekavelo.userservice.repository.UserRepository;

import java.util.UUID;

import static ru.tw1.euchekavelo.userservice.exception.enums.ExceptionMessage.USER_NOT_FOUND_EXCEPTION_MESSAGE;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthServiceApiAdapter authServiceApiAdapter;
    private final AuthorizationService authorizationService;

    @Transactional(rollbackFor = Throwable.class)
    public User createUser(User user) {
        String password = user.getPassword();
        user.setPassword(null);
        User savedUser = userRepository.save(user);

        User userToApi = userMapper.userToUser(new User(), savedUser);
        userToApi.setPassword(password);
        authServiceApiAdapter.createUser(userToApi);

        return savedUser;
    }


    public User getUserById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> {
            log.error(USER_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
            return new UserNotFoundException(USER_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
        });
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }

    @Transactional(rollbackFor = Throwable.class)
    public User updateUserById(UUID id, User user) {
        authorizationService.checkAccess(id);

        User existingUser = getUserById(id);
        UUID externalUserId = authServiceApiAdapter.getUserById(id).getId();

        User updatedUserWithFields = userMapper.userToUser(existingUser, user);
        User updatedUser = userRepository.save(updatedUserWithFields);

        authServiceApiAdapter.updateUserById(externalUserId, user);

        return updatedUser;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void deleteUserById(UUID id) {
        authorizationService.checkAccess(id);

        User user = getUserById(id);
        userRepository.delete(user);

        UUID externalUserId = authServiceApiAdapter.getUserById(id).getId();
        authServiceApiAdapter.deleteUserById(externalUserId);
    }
}
