package ru.skillbox.userservice.service;

import org.springframework.web.multipart.MultipartFile;
import ru.skillbox.userservice.dto.response.UserPhotoResponseDto;
import ru.skillbox.userservice.exception.*;

import java.io.IOException;
import java.util.UUID;

public interface PhotoService {

    UserPhotoResponseDto createUserPhoto(UUID userId, MultipartFile file) throws UserNotFoundException,
            IncorrectFileContentException, IncorrectFileFormatException, IOException;

    UserPhotoResponseDto getUserPhotoById(UUID userId, UUID photoId) throws PhotoNotFoundException;

    void deleteUserPhotoById(UUID userId, UUID photoId) throws PhotoNotFoundException, IOException;
}
