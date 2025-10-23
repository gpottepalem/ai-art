package com.giri.aiart.modules.storage;

import com.giri.aiart.config.MinioProperties;
import com.giri.aiart.shared.util.LogIcons;
import io.minio.*;
import io.minio.errors.MinioException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

/// Implementation of [`StorageService`](./StorageService.java) backed by **MinIO**.
///
/// This service provides functionality for uploading and downloading files
/// (objects) to and from a configured MinIO bucket.
/// It abstracts away direct MinIO SDK usage and provides a clean interface
/// for other parts of the application.
///
/// ### Features
/// - Automatically creates the target bucket if it does not exist
/// - Supports streaming uploads and downloads
/// - Can be easily replaced with other `StorageService` implementations (e.g., AWS S3)
///
/// ### Example
/// ```java
/// storageService.uploadFile("images/art1.png", inputStream, "image/png");
/// InputStream in = storageService.downloadFile("images/art1.png");
/// ```
/// @author Giri Pottepalem
@Slf4j
@Service
public class MinioStorageService implements StorageService {
    private final MinioClient minioClient;
    private final String bucketName;

    MinioStorageService(MinioClient minioClient, MinioProperties minioProperties) {
        this.minioClient = minioClient;
        this.bucketName = minioProperties.getBucketName();
        log.info("{} MinIO Properties loaded: endpoint={}, bucketName={}", LogIcons.TEXT, minioProperties.getEndpoint(), bucketName);
    }

    @PostConstruct
    public void verifyConnection() {
        try {
            var buckets = minioClient.listBuckets();
            log.info("{} Connected to MinIO, buckets: {}...", LogIcons.SUCCESS, buckets);
        } catch (Exception e) {
            log.error("{} Could not connect to MinIO...", LogIcons.ERROR, e);
        }
    }

    @Override
    public String uploadFile(MultipartFile file, String prefix) throws Exception {
        var objectKey = prefix + UUID.randomUUID() + "_" + file.getOriginalFilename();
        uploadFile(objectKey, file.getInputStream(), file.getContentType());
        log.info("🖼️ Uploaded file to MinIO: {}", objectKey);
        return objectKey;
    }

    @Override
    public void uploadFile(String objectName, InputStream inputStream, String contentType) throws Exception {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
            byte[] bytes = inputStream.readAllBytes();
            try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)) {
                minioClient.putObject(
                    PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(byteArrayInputStream, bytes.length, 5 * 1024 * 1024) // part size 5MB
                        .contentType(contentType)
                        .build()
                );
            }

        } catch(MinioException e) {
            log.error("{} Error uploading file to MinIO", LogIcons.ERROR, e);
            throw new RuntimeException("Error uploading file to MinIO", e);
        }
    }

    @Override
    public InputStream downloadFile(String objectName) throws Exception {
        return minioClient.getObject(
            GetObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build()
        );
    }
}
