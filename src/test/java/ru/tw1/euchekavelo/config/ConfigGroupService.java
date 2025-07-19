package ru.tw1.euchekavelo.config;

import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.tw1.euchekavelo.mapper.GroupMapper;
import ru.tw1.euchekavelo.repository.GroupRepository;

@TestConfiguration
public class ConfigGroupService {

    @Bean
    public GroupRepository groupRepository() {
        return Mockito.mock(GroupRepository.class);
    }

    @Bean
    public GroupMapper groupMapper() {
        return Mappers.getMapper(GroupMapper.class);
    }

    @Bean
    public GroupService groupService() {
        return new GroupService(groupRepository(), groupMapper());
    }
}
