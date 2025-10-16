package com.giri.aiart.modules.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/// Interface defining a generic storage service abstraction.
///
/// Designed to abstract away implementation details of object storage systems
/// such as MinIO, Amazon S3, Google Cloud Storage, or Azure Blob.
/// This allows the application to switch providers or mock storage
/// behavior for testing without changing business logic.
///
/// Typical implementations include:
/// - `MinioStorageService` (for self-hosted MinIO)
/// - `AwsS3StorageService` (for AWS S3)
/// - `InMemoryStorageService` (for testing)
/// @author Giri Pottepalem
public interface StorageService {

    /// Upload a file to MinIO with a generated object key
    /// @param file the file to upload
    /// @param prefix optional prefix/folder in MinIO bucket (e.g., "artworks/")
    /// @return the generated MinIO object key
    String uploadFile(MultipartFile file, String prefix) throws Exception;

    /// Uploads a file (object) to the configured storage bucket.
    ///
    /// ### Parameters
    /// - `objectName` – the key or file name under which the object will be stored.
    /// - `inputStream` – the input stream containing the object data to upload.
    /// - `contentType` – the MIME type of the object (e.g., `image/png`, `application/pdf`).
    void uploadFile(String objectName, InputStream inputStream, String contentType) throws Exception;

    /// Downloads a file (object) from the storage bucket.
    ///
    /// ### Parameters
    /// - `objectName` – the key or file name of the object to retrieve.
    ///
    /// ### Returns
    /// - An `InputStream` to read the object’s data.
    ///
    /// ### Throws
    /// - `Exception` – if the object does not exist or a network/storage error occurs.
    ///
    /// ### Example
    /// ```java
    /// try (InputStream in = storageService.downloadFile("artworks/sunset.png")) {
    ///     Files.copy(in, Path.of("downloaded.png"));
    /// }
    /// ```
    InputStream downloadFile(String objectName) throws Exception;
}
