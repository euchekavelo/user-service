package ru.skillbox.userservice.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.skillbox.userservice.repository.*;
import ru.skillbox.userservice.service.UserService;
import ru.skillbox.userservice.service.impl.UserServiceImpl;

@TestConfiguration
public class ConfigUserServiceTest {

    @Bean
    public UserRepository userRepository() {
        return Mockito.mock(UserRepository.class);
    }

    @Bean
    public TownRepository townRepository() {
        return Mockito.mock(TownRepository.class);
    }

    @Bean
    public UserSubscriptionRepository userSubscriptionRepository() {
        return Mockito.mock(UserSubscriptionRepository.class);
    }

    @Bean
    public GroupRepository groupRepository() {
        return Mockito.mock(GroupRepository.class);
    }

    @Bean
    public UserGroupRepository userGroupRepository() {
        return Mockito.mock(UserGroupRepository.class);
    }

    @Bean
    public UserService userService() {
        return new UserServiceImpl(userRepository(), townRepository(), userSubscriptionRepository(),
                groupRepository(), userGroupRepository());
    }
}
