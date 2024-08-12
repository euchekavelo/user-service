package ru.skillbox.userservice.config;

import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.skillbox.userservice.mapper.GroupMapper;
import ru.skillbox.userservice.repository.GroupRepository;
import ru.skillbox.userservice.service.GroupService;
import ru.skillbox.userservice.service.impl.GroupServiceImpl;

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
        return new GroupServiceImpl(groupRepository(), groupMapper());
    }
}
