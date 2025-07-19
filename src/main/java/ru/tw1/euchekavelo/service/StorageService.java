package ru.tw1.euchekavelo.service;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.tw1.euchekavelo.config.properties.S3MinioProperties;
import ru.tw1.euchekavelo.exception.IncorrectFileContentException;
import ru.tw1.euchekavelo.exception.IncorrectFileFormatException;
import ru.tw1.euchekavelo.repository.S3Repository;
import ru.tw1.euchekavelo.service.application.PhotoApplicationService;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static ru.tw1.euchekavelo.exception.enums.ExceptionMessage.EMPTY_FILE_EXCEPTION_MESSAGE;
import static ru.tw1.euchekavelo.exception.enums.ExceptionMessage.INVALID_FILE_EXCEPTION_MESSAGE;

@Observed
@Service
@RequiredArgsConstructor
public class StorageService {

    private final S3Repository s3Repository;
    private static final List<String> CORRECT_FILE_FORMATS = List.of("PNG", "JPEG", "JPG");
    private static final Logger LOGGER = LoggerFactory.getLogger(StorageService.class);

    public void uploadFile(MultipartFile file, String fileName) {
        if (file.isEmpty()) {
            throw new IncorrectFileContentException(EMPTY_FILE_EXCEPTION_MESSAGE.getExceptionMessage());
        }

        if (!isValidFormatFile(file)) {
            String enumerationFileFormats = String.join(", ", CORRECT_FILE_FORMATS);
            String errorMessage = INVALID_FILE_EXCEPTION_MESSAGE.getExceptionMessage() + " Recommended formats: "
                    + enumerationFileFormats + ".";

            LOGGER.error(errorMessage);
            throw new IncorrectFileFormatException(errorMessage);
        }

        try (InputStream inputStream = file.getInputStream()) {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(file.getContentType());
            objectMetadata.setContentLength(file.getSize());

            s3Repository.put(fileName, inputStream, objectMetadata);
        } catch (IOException e) {
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
            LOGGER.error(ex.getMessage());
            throw ex;
        }
    }

    private void uploadS3Object(S3Object s3Object) {
        try (InputStream inputStream = s3Object.getObjectContent()) {
            s3Repository.put(s3Object.getKey(), inputStream, s3Object.getObjectMetadata());
        } catch (IOException ex) {
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
