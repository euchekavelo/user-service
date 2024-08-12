package ru.skillbox.userservice.config;

import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.skillbox.userservice.config.properties.S3MinioProperties;
import ru.skillbox.userservice.mapper.GroupMapper;
import ru.skillbox.userservice.mapper.PhotoMapper;
import ru.skillbox.userservice.mapper.TownMapper;
import ru.skillbox.userservice.mapper.UserMapper;
import ru.skillbox.userservice.repository.*;
import ru.skillbox.userservice.service.UserService;
import ru.skillbox.userservice.service.impl.UserServiceImpl;

@TestConfiguration
@EnableConfigurationProperties
public class ConfigUserService {

    @Bean
    public S3MinioProperties s3MinioProperties() {
        return new S3MinioProperties();
    }

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
    public UserMapper userMapper() {
        return Mappers.getMapper(UserMapper.class);
    }

    @Bean
    public GroupMapper groupMapper() {
        return Mappers.getMapper(GroupMapper.class);
    }

    @Bean
    public TownMapper townMapper() {
        return Mappers.getMapper(TownMapper.class);
    }

    @Bean
    public PhotoMapper photoMapper() {
        return Mappers.getMapper(PhotoMapper.class);
    }

    @Bean
    public UserService userService() {
        return new UserServiceImpl(userRepository(), townRepository(), userSubscriptionRepository(),
                groupRepository(), userGroupRepository(), userMapper());
    }
}
