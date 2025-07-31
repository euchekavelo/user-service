package ru.tw1.euchekavelo.config;

import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.tw1.euchekavelo.mapper.GroupMapper;
import ru.tw1.euchekavelo.model.Group;
import ru.tw1.euchekavelo.security.util.UserDetailsContextUtil;
import ru.tw1.euchekavelo.service.EntityAccessCheckService;
import ru.tw1.euchekavelo.service.application.GroupApplicationService;
import ru.tw1.euchekavelo.service.domain.GroupDomainService;
import ru.tw1.euchekavelo.service.domain.UserDomainService;

@TestConfiguration
public class ConfigGroupApplicationService {

    @Bean
    public GroupMapper groupMapper() {
        return Mappers.getMapper(GroupMapper.class);
    }

    @Bean
    public GroupDomainService groupDomainService() {
        return Mockito.mock(GroupDomainService.class);
    }

    @Bean
    public UserDetailsContextUtil userDetailsContextUtil() {
        return Mockito.mock(UserDetailsContextUtil.class);
    }

    @Bean
    public UserDomainService userDomainService() {
        return Mockito.mock(UserDomainService.class);
    }

    @Bean
    public EntityAccessCheckService<Group> entityAccessCheckService() {
        return Mockito.mock(EntityAccessCheckService.class);
    }

    @Bean
    public GroupApplicationService groupApplicationService() {
        return new GroupApplicationService(userDetailsContextUtil(), userDomainService(), groupDomainService(),
                groupMapper(), entityAccessCheckService());
    }
}
