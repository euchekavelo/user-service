package ru.tw1.euchekavelo.service;

import com.amazonaws.services.s3.model.S3Object;
import org.hibernate.HibernateException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.http.MediaTypeFactory;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;
import ru.tw1.euchekavelo.config.ConfigPhotoService;
import ru.tw1.euchekavelo.dto.response.UserPhotoResponseDto;
import ru.tw1.euchekavelo.exception.IncorrectFileContentException;
import ru.tw1.euchekavelo.exception.IncorrectFileFormatException;
import ru.tw1.euchekavelo.exception.PhotoNotFoundException;
import ru.tw1.euchekavelo.exception.UserNotFoundException;
import ru.tw1.euchekavelo.model.Photo;
import ru.tw1.euchekavelo.model.User;
import ru.tw1.euchekavelo.model.enums.Sex;
import ru.tw1.euchekavelo.repository.PhotoRepository;
import ru.tw1.euchekavelo.repository.S3Repository;
import ru.tw1.euchekavelo.repository.UserRepository;
import ru.tw1.euchekavelo.service.application.PhotoApplicationService;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ConfigPhotoService.class, initializers = ConfigDataApplicationContextInitializer.class)
public class PhotoApplicationServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private PhotoApplicationService photoApplicationService;

    @Autowired
    private S3Repository s3Repository;

    private static UUID userId;
    private static UUID photoId;
    private static User user;
    private static Photo photo;
    private static File correctFile;
    private static File incorrectFile;

    @BeforeAll
    static void beforeAll() {
        userId = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");
        photoId = UUID.fromString("2fa22f22-2222-2222-b1fc-2c222f22afa2");

        user = new User();
        user.setId(userId);
        user.setEmail("invanov_test@gmail.com");
        user.setFullName("Ivanov Ivan Ivanovich");
        user.setSex(Sex.MALE);

        correctFile = new File("src/test/resources/files/correct_file.png");
        incorrectFile = new File("src/test/resources/files/incorrect_file.txt");

        LocalDateTime currentTime = LocalDateTime.now();
        photo = new Photo();
        photo.setId(photoId);
        photo.setName("test_file.png");
        photo.setLink("/test_url_file");
        photo.setCreationDate(currentTime);
        photo.setModificationDate(currentTime);
    }

    @Test
    void createUserPhotoSuccessfulTest() throws IOException, UserNotFoundException, IncorrectFileContentException,
            IncorrectFileFormatException {

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);
        MultipartFile multipartFile = getMockMultipartFile(correctFile);
        UserPhotoResponseDto userPhotoResponseDto = photoApplicationService.createUserPhoto(userId, multipartFile);

        assertThat(userPhotoResponseDto.getName()).isNotNull();
    }

    @Test
    void replaceOldPhotoSuccessfulTest() throws IOException, UserNotFoundException, IncorrectFileContentException,
            IncorrectFileFormatException {

        LocalDateTime oldDate = LocalDateTime.of(2024, 1, 1, 1, 11, 11);
        Photo oldPhoto = new Photo();
        oldPhoto.setId(photoId);
        oldPhoto.setName("test_file01012024.png");
        oldPhoto.setLink("/test_url_file");
        oldPhoto.setCreationDate(oldDate);
        oldPhoto.setModificationDate(oldDate);
        user.setPhoto(oldPhoto);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);
        MultipartFile multipartFile = getMockMultipartFile(correctFile);

        assertThat(photoApplicationService.createUserPhoto(userId, multipartFile).getName()).contains("correct_file.png");
    }

    @Test
    void replaceOldPhotoThrowHibernateExceptionTest() throws IOException {
        LocalDateTime oldDate = LocalDateTime.of(2024, 1, 1, 1, 11, 11);
        Photo oldPhoto = new Photo();
        oldPhoto.setId(photoId);
        oldPhoto.setName("test_file01012024.png");
        oldPhoto.setLink("/test_url_file");
        oldPhoto.setCreationDate(oldDate);
        oldPhoto.setModificationDate(oldDate);
        user.setPhoto(oldPhoto);

        Mockito.when(s3Repository.get(oldPhoto.getName())).thenReturn(Optional.of(getS3ObjectForTest()));

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(user)).thenThrow(HibernateException.class);
        MultipartFile multipartFile = getMockMultipartFile(correctFile);

        assertThrows(HibernateException.class, () -> photoApplicationService.createUserPhoto(userId, multipartFile));
    }

    @Test
    void createUserPhotoThrowUserNotFoundExceptionTest() throws IOException {
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());
        MultipartFile multipartFile = getMockMultipartFile(correctFile);

        assertThrows(UserNotFoundException.class, () -> photoApplicationService.createUserPhoto(userId, multipartFile));
    }

    @Test
    void createUserPhotoThrowIncorrectFileContentExceptionTest() {
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        MultipartFile emptyFile = new MockMultipartFile(" ", "", "", new byte[0]);

        assertThrows(IncorrectFileContentException.class, () -> photoApplicationService.createUserPhoto(userId, emptyFile));
    }

    @Test
    void createUserPhotoThrowIncorrectFileFormatException() throws IOException {
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        MultipartFile multipartFile = getMockMultipartFile(incorrectFile);

        assertThrows(IncorrectFileFormatException.class, () -> photoApplicationService.createUserPhoto(userId, multipartFile));
    }

    @Test
    void getUserPhotoByIdSuccessfulTest() throws PhotoNotFoundException {
        Mockito.when(photoRepository.findPhotoByUserIdAndId(userId, photoId)).thenReturn(Optional.of(photo));

        assertThat(photoApplicationService.getUserPhotoById(userId, photoId).getName()).isNotBlank();
    }

    @Test
    void getUserPhotoByIdThrowPhotoNotFoundExceptionTest() {
        Mockito.when(photoRepository.findPhotoByUserIdAndId(userId, photoId)).thenReturn(Optional.empty());

        assertThrows(PhotoNotFoundException.class, () -> photoApplicationService.getUserPhotoById(userId, photoId));
    }

    @Test
    void deleteUserPhotoByIdSuccessfulTest() {
        Mockito.when(photoRepository.findPhotoByUserIdAndId(userId, photoId)).thenReturn(Optional.of(photo));

        assertDoesNotThrow(() -> photoApplicationService.deleteUserPhotoById(userId, photoId));
    }

    @Test
    void deleteUserPhotoByIdThrowPhotoNotFoundExceptionTest() {
        Mockito.when(photoRepository.findPhotoByUserIdAndId(userId, photoId)).thenReturn(Optional.empty());

        assertThrows(PhotoNotFoundException.class, () -> photoApplicationService.deleteUserPhotoById(userId, photoId));
    }

    @Test
    void deleteUserPhotoByIdThrowHibernateExceptionTest() throws IOException {
        Mockito.when(photoRepository.findPhotoByUserIdAndId(userId, photoId)).thenReturn(Optional.of(photo));
        Mockito.when(s3Repository.get(photo.getName())).thenReturn(Optional.of(getS3ObjectForTest()));
        Mockito.doThrow(HibernateException.class).when(photoRepository).delete(photo);

        assertThrows(HibernateException.class, () -> photoApplicationService.deleteUserPhotoById(userId, photoId));
    }

    private MultipartFile getMockMultipartFile(File file) throws IOException {
        try (InputStream inputStream = new FileInputStream(file)) {
            String contentType = MediaTypeFactory.getMediaType(file.getName()).toString();

            return new MockMultipartFile(file.getName(), file.getName(), contentType, inputStream);
        }
    }

    private S3Object getS3ObjectForTest() throws IOException {
        try (InputStream inputStream = new FileInputStream("src/test/resources/files/correct_file.png")) {
            S3Object s3Object = new S3Object();
            s3Object.setObjectContent(inputStream);
            return s3Object;
        }
    }
}
