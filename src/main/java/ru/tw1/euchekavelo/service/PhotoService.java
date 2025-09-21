package ru.tw1.euchekavelo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.tw1.euchekavelo.config.properties.S3MinioProperties;
import ru.tw1.euchekavelo.exception.PhotoNotFoundException;
import ru.tw1.euchekavelo.model.Photo;
import ru.tw1.euchekavelo.model.User;
import ru.tw1.euchekavelo.repository.PhotoRepository;

import java.time.LocalDateTime;
import java.util.UUID;

import static ru.tw1.euchekavelo.exception.enums.ExceptionMessage.PHOTO_NOT_FOUND_EXCEPTION_MESSAGE;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final UserService userService;
    private final StorageService storageService;
    private final S3MinioProperties s3MinioProperties;
    private final EntityAccessCheckService<Photo> photoAccessCheckService;

    @Transactional(rollbackFor = Throwable.class)
    public Photo createUserPhoto(UUID userId, MultipartFile file) {
        User user = userService.getUserById(userId);
        String uniqueFileName = generateUniqueFileName(userId, file);
        String shortLinkForFile = generateShortLinkForPhoto(uniqueFileName);

        Photo photo = new Photo();
        photo.setUser(user);
        photo.setLink(shortLinkForFile);
        photo.setName(uniqueFileName);

        Photo savedPhoto = photoRepository.save(photo);
        user.setPhoto(savedPhoto);
        userService.updateUser(user);
        storageService.uploadFile(file, uniqueFileName);

        return savedPhoto;
    }

    public Photo getUserPhotoById(UUID userId, UUID photoId) {
        return photoRepository.findPhotoByUserIdAndId(userId, photoId).orElseThrow(() -> {
            log.error(PHOTO_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
            return new PhotoNotFoundException(PHOTO_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
        });
    }

    @Transactional(rollbackFor = Throwable.class)
    public void deleteUserPhotoById(UUID userId, UUID photoId) {
        Photo photo = getUserPhotoById(userId, photoId);
        photoAccessCheckService.checkEntityAccess(photo);
        User user = photo.getUser();
        user.setPhoto(null);
        userService.updateUser(user);
        storageService.deleteFileByName(photo.getName());

        photoRepository.delete(photo);
    }

    private String generateShortLinkForPhoto(String fileName) {
        return s3MinioProperties.getBucketUsers() + "/" + fileName;
    }

    private String generateUniqueFileName(UUID userId, MultipartFile file) {
        return userId.toString() + "_" + LocalDateTime.now() + "_" + file.getOriginalFilename();
    }
}
