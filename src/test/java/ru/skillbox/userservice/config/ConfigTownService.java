package ru.skillbox.userservice.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.skillbox.userservice.repository.TownRepository;
import ru.skillbox.userservice.service.TownService;
import ru.skillbox.userservice.service.impl.TownServiceImpl;

@TestConfiguration
public class ConfigTownService {

    @Bean
    public TownRepository townRepository() {
        return Mockito.mock(TownRepository.class);
    }

    @Bean
    public TownService townService() {
        return new TownServiceImpl(townRepository());
    }
}
