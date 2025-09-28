package ru.tw1.euchekavelo.userservice.service;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.tw1.euchekavelo.userservice.exception.IncorrectFileFormatException;
import ru.tw1.euchekavelo.userservice.repository.S3Repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static ru.tw1.euchekavelo.userservice.exception.enums.ExceptionMessage.INVALID_FILE_EXCEPTION_MESSAGE;

@Observed
@Service
@RequiredArgsConstructor
@Slf4j
public class StorageService {

    private final S3Repository s3Repository;
    private static final List<String> CORRECT_FILE_FORMATS = List.of("PNG", "JPEG", "JPG");

    public void uploadFile(MultipartFile file, String fileName) {
        if (!isValidFormatFile(file)) {
            String enumerationFileFormats = String.join(", ", CORRECT_FILE_FORMATS);
            String errorMessage = INVALID_FILE_EXCEPTION_MESSAGE.getExceptionMessage() + " Рекомендуемые форматы: "
                    + enumerationFileFormats + ".";

            log.error(errorMessage);
            throw new IncorrectFileFormatException(errorMessage);
        }

        try (InputStream inputStream = file.getInputStream()) {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(file.getContentType());
            objectMetadata.setContentLength(file.getSize());

            s3Repository.put(fileName, inputStream, objectMetadata);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void deleteFileByName(String fileName) {
        Optional<S3Object> optionalS3OldObject = Optional.empty();

        try {
            optionalS3OldObject = s3Repository.get(fileName);
            s3Repository.delete(fileName);
        } catch (Exception ex) {
            optionalS3OldObject.ifPresent(this::uploadS3Object);
            log.error(ex.getMessage());
            throw ex;
        }
    }

    private void uploadS3Object(S3Object s3Object) {
        try (InputStream inputStream = s3Object.getObjectContent()) {
            s3Repository.put(s3Object.getKey(), inputStream, s3Object.getObjectMetadata());
        } catch (IOException ex) {
            log.error(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    private boolean isValidFormatFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String fileExtension = Objects.requireNonNull(fileName).substring(fileName.lastIndexOf(".") + 1);

        return CORRECT_FILE_FORMATS.stream()
                .anyMatch(format -> format.equals(fileExtension.toUpperCase()));
    }
}
