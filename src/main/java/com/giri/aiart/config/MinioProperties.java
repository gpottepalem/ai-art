package com.giri.aiart.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/// MinIO client type-safe property mapping.
/// @author Giri Pottepalem
@Data
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private boolean secure;
    private String bucketName;
    private String imageSize;
    private String fileSize;
}
