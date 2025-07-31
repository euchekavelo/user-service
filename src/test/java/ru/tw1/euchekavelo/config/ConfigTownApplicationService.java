package ru.tw1.euchekavelo.config;

import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.tw1.euchekavelo.mapper.TownMapper;
import ru.tw1.euchekavelo.service.application.TownApplicationService;
import ru.tw1.euchekavelo.service.domain.TownDomainService;

@TestConfiguration
public class ConfigTownApplicationService {

    @Bean
    public TownDomainService townDomainService() {
        return Mockito.mock(TownDomainService.class);
    }

    @Bean
    public TownMapper townMapper() {
        return Mappers.getMapper(TownMapper.class);
    }

    @Bean
    public TownApplicationService townApplicationService() {
        return new TownApplicationService(townDomainService(), townMapper());
    }
}
