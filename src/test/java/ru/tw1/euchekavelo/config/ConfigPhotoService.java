package ru.tw1.euchekavelo.config;

import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.tw1.euchekavelo.config.properties.S3MinioProperties;
import ru.tw1.euchekavelo.mapper.PhotoMapper;
import ru.tw1.euchekavelo.repository.PhotoRepository;
import ru.tw1.euchekavelo.repository.S3Repository;
import ru.tw1.euchekavelo.repository.UserRepository;
import ru.tw1.euchekavelo.service.application.PhotoApplicationService;

@TestConfiguration
@EnableConfigurationProperties
public class ConfigPhotoService {

    @Bean
    public S3Repository s3Repository() {
        return Mockito.mock(S3Repository.class);
    }

    @Bean
    public PhotoRepository photoRepository() {
        return Mockito.mock(PhotoRepository.class);
    }

    @Bean
    public S3MinioProperties s3MinioProperties() {
        return new S3MinioProperties();
    }

    @Bean
    public PhotoMapper photoMapper() {
        return Mappers.getMapper(PhotoMapper.class);
    }

    @Bean
    public UserRepository userRepository() {
        return Mockito.mock(UserRepository.class);
    }

    @Bean
    public PhotoApplicationService photoService() {
        return new PhotoApplicationService(s3Repository(), photoRepository(), s3MinioProperties(),
                photoMapper(), userRepository());
    }
}
