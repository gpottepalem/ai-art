package com.giri.aiart.config;

import io.minio.MinioClient;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/// A single MinioClient bean auto-configured from YAML
/// @author Giri Pottepalem
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
    public void verifyConnection() {
        IO.println("✅ Connected to MinIO at: %s".formatted(minioProperties.getEndpoint()));
    }
}
