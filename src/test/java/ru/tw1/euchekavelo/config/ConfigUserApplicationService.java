package ru.tw1.euchekavelo.config;

import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.tw1.euchekavelo.client.api.AuthServiceApiClient;
import ru.tw1.euchekavelo.config.properties.S3MinioProperties;
import ru.tw1.euchekavelo.mapper.GroupMapper;
import ru.tw1.euchekavelo.mapper.PhotoMapper;
import ru.tw1.euchekavelo.mapper.TownMapper;
import ru.tw1.euchekavelo.mapper.UserMapper;
import ru.tw1.euchekavelo.service.AuthorizationService;
import ru.tw1.euchekavelo.service.application.UserApplicationService;
import ru.tw1.euchekavelo.service.domain.*;

@TestConfiguration
@EnableConfigurationProperties
public class ConfigUserApplicationService {

    @Bean
    public S3MinioProperties s3MinioProperties() {
        return new S3MinioProperties();
    }

    @Bean
    public UserDomainService userDomainService() {
        return Mockito.mock(UserDomainService.class);
    }

    @Bean
    public UserGroupDomainService userGroupDomainService() {
        return Mockito.mock(UserGroupDomainService.class);
    }

    @Bean
    public UserSubscriptionDomainService userSubscriptionDomainService() {
        return Mockito.mock(UserSubscriptionDomainService.class);
    }

    @Bean
    public TownDomainService townDomainService() {
        return Mockito.mock(TownDomainService.class);
    }

    @Bean
    public GroupDomainService groupDomainService() {
        return Mockito.mock(GroupDomainService.class);
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
    public AuthServiceApiClient authServiceApiClient() {
        return Mockito.mock(AuthServiceApiClient.class);
    }

    @Bean
    public AuthorizationService authorizationService() {
        return Mockito.mock(AuthorizationService.class);
    }

    @Bean
    public UserApplicationService userApplicationService() {
        return new UserApplicationService(userDomainService(), userGroupDomainService(), userSubscriptionDomainService(),
                townDomainService(), groupDomainService(), userMapper(), authServiceApiClient(), authorizationService());
    }
}
