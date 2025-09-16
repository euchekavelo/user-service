package ru.tw1.euchekavelo.service.facade;

import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.tw1.euchekavelo.config.properties.S3MinioProperties;
import ru.tw1.euchekavelo.dto.response.UserPhotoResponseDto;
import ru.tw1.euchekavelo.mapper.PhotoMapper;
import ru.tw1.euchekavelo.model.Photo;
import ru.tw1.euchekavelo.model.User;
import ru.tw1.euchekavelo.service.EntityAccessCheckService;
import ru.tw1.euchekavelo.service.StorageService;
import ru.tw1.euchekavelo.service.domain.PhotoDomainService;
import ru.tw1.euchekavelo.service.domain.UserDomainService;

import java.time.LocalDateTime;
import java.util.UUID;

@Observed
@Service
@RequiredArgsConstructor
public class PhotoFacadeService {

    private final S3MinioProperties s3MinioProperties;
    private final PhotoDomainService photoDomainService;
    private final PhotoMapper photoMapper;
    private final UserDomainService userDomainService;
    private final StorageService storageService;
    private final EntityAccessCheckService<Photo> photoAccessCheckService;

    @Transactional(rollbackFor = Throwable.class)
    public UserPhotoResponseDto createUserPhoto(UUID userId, MultipartFile file) {
        User user = userDomainService.findUserById(userId);
        String uniqueFileName = generateUniqueFileName(userId, file);
        String shortLinkForFile = generateShortLinkForPhoto(uniqueFileName);
        Photo photo = photoDomainService.savePhoto(user, shortLinkForFile, uniqueFileName);
        user.setPhoto(photo);
        userDomainService.updateUser(user);
        storageService.uploadFile(file, uniqueFileName);

        return photoMapper.photoToUserPhotoResponseDto(photo);
    }

    public UserPhotoResponseDto getUserPhotoById(UUID userId, UUID photoId) {
        Photo photo = photoDomainService.getPhotoByIdAndUserId(photoId, userId);

        return photoMapper.photoToUserPhotoResponseDto(photo);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void deleteUserPhotoById(UUID userId, UUID photoId) {
        Photo photo = photoDomainService.getPhotoByIdAndUserId(photoId, userId);
        photoAccessCheckService.checkEntityAccess(photo);
        photoDomainService.deletePhoto(photo);

        storageService.deleteFileByName(photo.getName());
    }

    private String generateShortLinkForPhoto(String fileName) {
        return s3MinioProperties.getBucketUsers() + "/" + fileName;
    }

    private String generateUniqueFileName(UUID userId, MultipartFile file) {
        return userId.toString() + "_" + LocalDateTime.now() + "_" + file.getOriginalFilename();
    }
}
