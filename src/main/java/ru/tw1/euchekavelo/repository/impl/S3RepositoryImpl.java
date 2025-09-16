package ru.tw1.euchekavelo.repository.impl;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.tw1.euchekavelo.config.properties.S3MinioProperties;
import ru.tw1.euchekavelo.repository.S3Repository;

import java.io.InputStream;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class S3RepositoryImpl implements S3Repository {

    private final AmazonS3 amazonS3Client;
    private final S3MinioProperties s3MinioProperties;

    @Override
    public void put(String name, InputStream inputStream, ObjectMetadata objectMetaData) {
        amazonS3Client.putObject(s3MinioProperties.getBucketUsers(), name, inputStream, objectMetaData);
    }

    @Override
    public void delete(String name) {
        amazonS3Client.deleteObject(s3MinioProperties.getBucketUsers(), name);
    }

    @Override
    public Optional<S3Object> get(String name) {
        try {
            return Optional.of(amazonS3Client.getObject(s3MinioProperties.getBucketUsers(), name));
        } catch (AmazonServiceException exception) {
            return Optional.empty();
        }
    }
}
