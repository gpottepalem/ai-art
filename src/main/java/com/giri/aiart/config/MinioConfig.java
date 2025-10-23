package com.giri.aiart.config;

import com.giri.aiart.shared.util.LogIcons;
import io.minio.MinioClient;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/// A single MinioClient bean auto-configured from YAML
/// @author Giri Pottepalem
@Slf4j
@Configuration
@EnableConfigurationProperties(MinioProperties.class)
@AllArgsConstructor
public class MinioConfig {
    private final MinioProperties minioProperties;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
            .endpoint(minioProperties.getEndpoint())
            .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
            .build();
    }

    @PostConstruct
    public void verifyConfig() {
        // TODO - don't log secrets
        log.info("{} MinIO client configured with properties: {}", LogIcons.SUCCESS, minioProperties);
    }
}
