package ru.skillbox.userservice.service.impl;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skillbox.userservice.config.properties.S3MinioProperties;
import ru.skillbox.userservice.dto.response.UserPhotoResponseDto;
import ru.skillbox.userservice.exception.*;
import ru.skillbox.userservice.mapper.PhotoMapper;
import ru.skillbox.userservice.model.Photo;
import ru.skillbox.userservice.model.User;
import ru.skillbox.userservice.repository.PhotoRepository;
import ru.skillbox.userservice.repository.S3Repository;
import ru.skillbox.userservice.repository.UserRepository;
import ru.skillbox.userservice.service.PhotoService;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static ru.skillbox.userservice.exception.enums.ExceptionMessage.*;

@Observed
@Service
public class PhotoServiceImpl implements PhotoService {

    private final S3Repository s3Repository;
    private final PhotoRepository photoRepository;
    private final S3MinioProperties s3MinioProperties;
    private final PhotoMapper photoMapper;
    private final UserRepository userRepository;
    private static final List<String> CORRECT_FILE_FORMATS = List.of("PNG", "JPEG", "JPG");
    private final Logger logger = LoggerFactory.getLogger(PhotoServiceImpl.class);

    @Autowired
    public PhotoServiceImpl(S3Repository s3Repository, PhotoRepository photoRepository,
                            S3MinioProperties s3MinioProperties, PhotoMapper photoMapper, UserRepository userRepository) {

        this.s3Repository = s3Repository;
        this.photoRepository = photoRepository;
        this.s3MinioProperties = s3MinioProperties;
        this.photoMapper = photoMapper;
        this.userRepository = userRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserPhotoResponseDto createUserPhoto(UUID userId, MultipartFile file) throws UserNotFoundException,
            IncorrectFileContentException, IncorrectFileFormatException, IOException {

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            logger.error(USER_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
            throw new UserNotFoundException(USER_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
        }

        if (file.isEmpty()) {
            throw new IncorrectFileContentException(EMPTY_FILE_EXCEPTION_MESSAGE.getExceptionMessage());
        }

        if (!isValidFormatFile(file)) {
            String enumerationFileFormats = String.join(", ", CORRECT_FILE_FORMATS);
            String errorMessage = INVALID_FILE_EXCEPTION_MESSAGE.getExceptionMessage() + " Recommended formats: "
                    + enumerationFileFormats + ".";

            logger.error(errorMessage);
            throw new IncorrectFileFormatException(errorMessage);
        }

        return setNewPhotoForTheUser(optionalUser.get(), file);
    }

    @Override
    public UserPhotoResponseDto getUserPhotoById(UUID userId, UUID photoId) throws PhotoNotFoundException {
        Optional<Photo> optionalPhoto = photoRepository.findPhotoByUserIdAndId(userId, photoId);
        if (optionalPhoto.isEmpty()) {
            logger.error(PHOTO_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
            throw new PhotoNotFoundException(PHOTO_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
        }

        return photoMapper.photoToUserPhotoResponseDto(optionalPhoto.get());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteUserPhotoById(UUID userId, UUID photoId) throws PhotoNotFoundException, IOException {
        Optional<Photo> optionalPhoto = photoRepository.findPhotoByUserIdAndId(userId, photoId);
        if (optionalPhoto.isEmpty()) {
            logger.error(PHOTO_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
            throw new PhotoNotFoundException(PHOTO_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
        }

        Photo photo = optionalPhoto.get();
        Optional<S3Object> optionalS3OldObject = Optional.empty();

        try {
            optionalS3OldObject = s3Repository.get(photo.getName());
            photoRepository.delete(photo);
            s3Repository.delete(photo.getName());
        } catch (Exception ex) {
            if (optionalS3OldObject.isPresent()) {
                uploadS3Object(optionalS3OldObject.get());
            }
            logger.error(ex.getMessage());
            throw ex;
        }
    }

    private String generateShortLinkForFile(String fileName) {
        return s3MinioProperties.getBucketPosts() + "/" + fileName;
    }

    private String generateUniqueFileNameForUser(UUID userId, MultipartFile file) {
        return userId.toString() + "_" + LocalDateTime.now() + "_" + file.getOriginalFilename();
    }

    private boolean isValidFormatFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String fileExtension = Objects.requireNonNull(fileName).substring(fileName.lastIndexOf(".") + 1);

        return CORRECT_FILE_FORMATS.stream()
                .anyMatch(format -> format.equals(fileExtension.toUpperCase()));
    }

    private Photo getNewUserPhotoToUploadToTheDatabase(MultipartFile file, String uniqueFileName, User user)
            throws IOException {

        try (InputStream inputStream = file.getInputStream()) {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(file.getContentType());
            objectMetadata.setContentLength(file.getSize());

            s3Repository.put(uniqueFileName, inputStream, objectMetadata);
        }

        Photo photo = new Photo();
        photo.setUser(user);
        photo.setLink(generateShortLinkForFile(uniqueFileName));
        photo.setName(uniqueFileName);

        return photo;
    }

    private void uploadS3Object(S3Object s3Object) throws IOException {
        try (InputStream inputStream = s3Object.getObjectContent()) {
            s3Repository.put(s3Object.getKey(), inputStream, s3Object.getObjectMetadata());
        }
    }

    private UserPhotoResponseDto setNewPhotoForTheUser(User user, MultipartFile file) throws IOException {
        Optional<Photo> optionalOldUserPhoto = Optional.ofNullable(user.getPhoto());
        String uniqueFileName = generateUniqueFileNameForUser(user.getId(), file);
        Optional<S3Object> optionalS3OldObject = Optional.empty();

        try {
            if (optionalOldUserPhoto.isPresent()) {
                Photo oldUserPhoto = optionalOldUserPhoto.get();
                optionalS3OldObject = s3Repository.get(oldUserPhoto.getName());
                photoRepository.delete(oldUserPhoto);
                s3Repository.delete(oldUserPhoto.getName());
            }

            Photo newPhoto = getNewUserPhotoToUploadToTheDatabase(file, uniqueFileName, user);
            user.setPhoto(newPhoto);
            User updatedUser = userRepository.save(user);

            return photoMapper.photoToUserPhotoResponseDto(updatedUser.getPhoto());
        } catch (Exception ex) {
            if (optionalS3OldObject.isPresent()) {
                uploadS3Object(optionalS3OldObject.get());
            }
            s3Repository.delete(uniqueFileName);
            logger.error(ex.getMessage());
            throw ex;
        }
    }
}
