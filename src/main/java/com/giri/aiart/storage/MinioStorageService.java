package com.giri.aiart.storage;

import com.giri.aiart.config.MinioProperties;
import io.minio.*;
import io.minio.errors.MinioException;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

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
@Service
public class MinioStorageService implements StorageService {
    private final MinioClient minioClient;
    private final String bucketName;

    MinioStorageService(MinioClient minioClient, MinioProperties minioProperties) {
        this.minioClient = minioClient;
        this.bucketName = minioProperties.getBucketName();
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
            throw new RuntimeException("Error uploading file to MinIO", e);
        }
    }

    @Override
    public InputStream downloadFile(String objectName) throws Exception {
        return minioClient.getObject(GetObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build());
    }
}
