package ru.skillbox.userservice.repository.impl;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.skillbox.userservice.config.properties.S3MinioProperties;
import ru.skillbox.userservice.repository.S3Repository;

import java.io.InputStream;
import java.util.Optional;

@Repository
public class S3RepositoryImpl implements S3Repository {

    private final AmazonS3 amazonS3Client;
    private final S3MinioProperties s3MinioProperties;

    @Autowired
    public S3RepositoryImpl(AmazonS3 amazonS3Client, S3MinioProperties s3MinioProperties) {
        this.amazonS3Client = amazonS3Client;
        this.s3MinioProperties = s3MinioProperties;
    }

    @Override
    public void put(String name, InputStream inputStream, ObjectMetadata objectMetaData) {
        amazonS3Client.putObject(s3MinioProperties.getBucketPosts(), name, inputStream, objectMetaData);
    }

    @Override
    public void delete(String name) {
        amazonS3Client.deleteObject(s3MinioProperties.getBucketPosts(), name);
    }

    @Override
    public Optional<S3Object> get(String name) {
        try {
            return Optional.of(amazonS3Client.getObject(s3MinioProperties.getBucketPosts(), name));
        } catch (AmazonServiceException exception) {
            return Optional.empty();
        }
    }
}
