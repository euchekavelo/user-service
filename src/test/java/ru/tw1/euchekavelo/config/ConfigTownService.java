package ru.tw1.euchekavelo.config;

import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.tw1.euchekavelo.mapper.TownMapper;
import ru.tw1.euchekavelo.repository.TownRepository;
import ru.tw1.euchekavelo.service.application.TownApplicationService;

@TestConfiguration
public class ConfigTownService {

    @Bean
    public TownRepository townRepository() {
        return Mockito.mock(TownRepository.class);
    }

    @Bean
    public TownMapper townMapper() {
        return Mappers.getMapper(TownMapper.class);
    }

    @Bean
    public TownApplicationService townService() {
        return new TownApplicationService(townRepository(), townMapper());
    }
}
