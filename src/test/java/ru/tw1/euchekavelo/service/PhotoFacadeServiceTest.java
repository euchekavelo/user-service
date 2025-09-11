package ru.tw1.euchekavelo.service;

import org.hibernate.HibernateException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
import ru.tw1.euchekavelo.config.ConfigPhotoApplicationService;
import ru.tw1.euchekavelo.dto.response.UserPhotoResponseDto;
import ru.tw1.euchekavelo.exception.IncorrectFileFormatException;
import ru.tw1.euchekavelo.exception.PhotoNotFoundException;
import ru.tw1.euchekavelo.exception.ResourceAccessDeniedException;
import ru.tw1.euchekavelo.exception.UserNotFoundException;
import ru.tw1.euchekavelo.model.Photo;
import ru.tw1.euchekavelo.model.User;
import ru.tw1.euchekavelo.model.enums.Sex;
import ru.tw1.euchekavelo.service.facade.PhotoFacadeService;
import ru.tw1.euchekavelo.service.domain.PhotoDomainService;
import ru.tw1.euchekavelo.service.domain.UserDomainService;

import java.io.*;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ConfigPhotoApplicationService.class, initializers = ConfigDataApplicationContextInitializer.class)
public class PhotoFacadeServiceTest {

    @Autowired
    private PhotoDomainService photoDomainService;

    @Autowired
    private UserDomainService userDomainService;

    @Autowired
    private StorageService storageService;

    @Autowired
    private EntityAccessCheckService<Photo> entityAccessCheckService;

    @Autowired
    private PhotoFacadeService photoFacadeService;

    private static UUID userId;
    private static UUID photoId;
    private static User user;
    private static Photo photo;
    private static File correctFile;
    private static File incorrectFile;

    @BeforeEach
    void resetMocks() {
        Mockito.reset(photoDomainService);
        Mockito.reset(userDomainService);
        Mockito.reset(storageService);
        Mockito.reset(entityAccessCheckService);
    }

    @BeforeAll
    static void beforeAll() {
        userId = UUID.randomUUID();
        photoId = UUID.randomUUID();
        user = getUser();
        photo = getPhoto();
        correctFile = new File("src/test/resources/files/correct_file.png");
        incorrectFile = new File("src/test/resources/files/incorrect_file.txt");
    }

    @Test
    void createUserPhotoSuccessfulTest() throws IOException {
        Mockito.when(userDomainService.findUserById(userId)).thenReturn(user);
        Mockito.when(photoDomainService.savePhoto(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(photo);
        MultipartFile multipartFile = getMockMultipartFile(correctFile);
        Mockito.doNothing().when(storageService).uploadFile(multipartFile, multipartFile.getName());
        UserPhotoResponseDto userPhotoResponseDto = photoFacadeService.createUserPhoto(userId, multipartFile);

        assertThat(userPhotoResponseDto.getName()).isNotNull();
    }

    @Test
    void createUserPhotoThrowHibernateExceptionTest() throws IOException {
        Mockito.when(userDomainService.findUserById(userId)).thenReturn(user);
        Mockito.when(photoDomainService.savePhoto(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenThrow(HibernateException.class);
        MultipartFile multipartFile = getMockMultipartFile(correctFile);

        assertThrows(HibernateException.class, () -> photoFacadeService.createUserPhoto(userId, multipartFile));
    }

    @Test
    void createUserPhotoThrowUserNotFoundExceptionTest() throws IOException {
        Mockito.when(userDomainService.findUserById(userId)).thenThrow(UserNotFoundException.class);
        MultipartFile multipartFile = getMockMultipartFile(correctFile);

        assertThrows(UserNotFoundException.class, () -> photoFacadeService.createUserPhoto(userId, multipartFile));
    }

    @Test
    void createUserPhotoThrowIncorrectFileFormatException() throws IOException {
        Mockito.when(userDomainService.findUserById(userId)).thenReturn(user);
        Mockito.when(photoDomainService.savePhoto(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(photo);
        MultipartFile multipartFile = getMockMultipartFile(incorrectFile);
        Mockito.doThrow(IncorrectFileFormatException.class).when(storageService).uploadFile(Mockito.any(), Mockito.any());

        assertThrows(IncorrectFileFormatException.class,
                () -> photoFacadeService.createUserPhoto(userId, multipartFile));
    }

    @Test
    void getUserPhotoByIdSuccessfulTest() {
        Mockito.when(photoDomainService.getPhotoByIdAndUserId(photoId, userId)).thenReturn(photo);

        assertThat(photoFacadeService.getUserPhotoById(userId, photoId).getName()).isNotBlank();
    }

    @Test
    void getUserPhotoByIdThrowPhotoNotFoundExceptionTest() {
        Mockito.when(photoDomainService.getPhotoByIdAndUserId(photoId, userId)).thenThrow(PhotoNotFoundException.class);

        assertThrows(PhotoNotFoundException.class, () -> photoFacadeService.getUserPhotoById(userId, photoId));
    }

    @Test
    void deleteUserPhotoByIdSuccessfulTest() {
        Mockito.when(photoDomainService.getPhotoByIdAndUserId(photoId, userId)).thenReturn(photo);

        assertDoesNotThrow(() -> photoFacadeService.deleteUserPhotoById(userId, photoId));
    }

    @Test
    void deleteUserPhotoByIdThrowPhotoNotFoundExceptionTest() {
        Mockito.when(photoDomainService.getPhotoByIdAndUserId(photoId, userId)).thenThrow(PhotoNotFoundException.class);

        assertThrows(PhotoNotFoundException.class, () -> photoFacadeService.deleteUserPhotoById(userId, photoId));
    }

    @Test
    void deleteUserPhotoByIdThrowHibernateExceptionTest() {
        Mockito.when(photoDomainService.getPhotoByIdAndUserId(photoId, userId)).thenReturn(photo);
        Mockito.doThrow(HibernateException.class).when(photoDomainService).deletePhoto(photo);

        assertThrows(HibernateException.class, () -> photoFacadeService.deleteUserPhotoById(userId, photoId));
    }

    @Test
    void deleteUserPhotoByIdThrowResourceAccessExceptionTest() {
        Mockito.when(photoDomainService.getPhotoByIdAndUserId(photoId, userId)).thenReturn(photo);
        Mockito.doThrow(ResourceAccessDeniedException.class).when(entityAccessCheckService).checkEntityAccess(photo);

        assertThrows(ResourceAccessDeniedException.class,
                () -> photoFacadeService.deleteUserPhotoById(userId, photoId));
    }

    private MultipartFile getMockMultipartFile(File file) throws IOException {
        try (InputStream inputStream = new FileInputStream(file)) {
            String contentType = MediaTypeFactory.getMediaType(file.getName()).toString();

            return new MockMultipartFile(file.getName(), file.getName(), contentType, inputStream);
        }
    }

    private static User getUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("invanov_test@gmail.com");
        user.setLastName("Ivanov");
        user.setFirstName("Ivan");
        user.setMiddleName("Ivanovich");
        user.setSex(Sex.MALE);

        return user;
    }

    private static Photo getPhoto() {
        LocalDateTime currentTime = LocalDateTime.now();
        Photo photo = new Photo();
        photo.setId(UUID.randomUUID());
        photo.setName("test_file.png");
        photo.setLink("/test_url_file");
        photo.setCreationDate(currentTime);
        photo.setModificationDate(currentTime);

        return photo;
    }
}
