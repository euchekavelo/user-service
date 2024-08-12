package ru.skillbox.userservice.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.skillbox.userservice.config.properties.S3MinioProperties;
import ru.skillbox.userservice.dto.response.UserPhotoResponseDto;
import ru.skillbox.userservice.model.Photo;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class PhotoMapper {

    @Autowired
    protected S3MinioProperties s3MinioProperties;

    public abstract UserPhotoResponseDto photoToUserPhotoResponseDto(Photo photo);

    @AfterMapping
    protected void setLink(@MappingTarget UserPhotoResponseDto postDtoResponse) {
        postDtoResponse.setLink(getActualEndpointLink().concat(postDtoResponse.getLink()));
    }

    private String getActualEndpointLink() {
        String endpointValue = s3MinioProperties.getEndpoint();
        return endpointValue.endsWith("/") ? endpointValue : endpointValue.concat("/");
    }
}
