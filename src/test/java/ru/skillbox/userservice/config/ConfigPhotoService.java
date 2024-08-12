package ru.skillbox.userservice.config;

import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.skillbox.userservice.config.properties.S3MinioProperties;
import ru.skillbox.userservice.mapper.PhotoMapper;
import ru.skillbox.userservice.repository.PhotoRepository;
import ru.skillbox.userservice.repository.S3Repository;
import ru.skillbox.userservice.repository.UserRepository;
import ru.skillbox.userservice.service.PhotoService;
import ru.skillbox.userservice.service.impl.PhotoServiceImpl;

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
    public PhotoService photoService() {
        return new PhotoServiceImpl(s3Repository(), photoRepository(), s3MinioProperties(),
                photoMapper(), userRepository());
    }
}
