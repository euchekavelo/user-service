package ru.tw1.euchekavelo.config;

import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.tw1.euchekavelo.config.properties.S3MinioProperties;
import ru.tw1.euchekavelo.mapper.PhotoMapper;
import ru.tw1.euchekavelo.model.Photo;
import ru.tw1.euchekavelo.service.EntityAccessCheckService;
import ru.tw1.euchekavelo.service.StorageService;
import ru.tw1.euchekavelo.service.facade.PhotoFacadeService;
import ru.tw1.euchekavelo.service.domain.PhotoDomainService;
import ru.tw1.euchekavelo.service.domain.UserDomainService;

@TestConfiguration
@EnableConfigurationProperties
public class ConfigPhotoApplicationService {

    @Bean
    public PhotoDomainService photoDomainService() {
        return Mockito.mock(PhotoDomainService.class);
    }

    @Bean
    public PhotoMapper photoMapper() {
        return Mappers.getMapper(PhotoMapper.class);
    }

    @Bean
    public UserDomainService userDomainService() {
        return Mockito.mock(UserDomainService.class);
    }

    @Bean
    public StorageService storageService() {
        return Mockito.mock(StorageService.class);
    }

    @Bean
    public EntityAccessCheckService<Photo> entityAccessCheckService() {
        return Mockito.mock(EntityAccessCheckService.class);
    }

    @Bean
    public S3MinioProperties s3MinioProperties() {
        return new S3MinioProperties();
    }

    @Bean
    public PhotoFacadeService photoApplicationService() {
        return new PhotoFacadeService(s3MinioProperties(), photoDomainService(), photoMapper(),
                userDomainService(), storageService(),entityAccessCheckService());
    }
}
