package ru.tw1.euchekavelo.userservice.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "aws.s3.minio")
@Data
public class S3MinioProperties {

    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String region;
    private String bucketUsers;
    private String signer;
}
