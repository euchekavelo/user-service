package ru.tw1.euchekavelo.service.domain;

import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.tw1.euchekavelo.exception.PhotoNotFoundException;
import ru.tw1.euchekavelo.model.Photo;
import ru.tw1.euchekavelo.model.User;
import ru.tw1.euchekavelo.repository.PhotoRepository;

import java.util.UUID;

import static ru.tw1.euchekavelo.exception.enums.ExceptionMessage.PHOTO_NOT_FOUND_EXCEPTION_MESSAGE;

@Observed
@Service
@RequiredArgsConstructor
public class PhotoDomainService {

    private final PhotoRepository photoRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(PhotoDomainService.class);

    public Photo getPhotoByIdAndUserId(UUID id, UUID userId) {
        return photoRepository.findPhotoByUserIdAndId(userId, id).orElseThrow(() -> {
            LOGGER.error(PHOTO_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
            return new PhotoNotFoundException(PHOTO_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
        });
    }

    public void deletePhoto(Photo photo) {
        photoRepository.delete(photo);
    }

    public Photo savePhoto(User user, String link, String fileName) {
        Photo photo = new Photo();
        photo.setUser(user);
        photo.setLink(link);
        photo.setName(fileName);

        return photoRepository.save(photo);
    }
}
