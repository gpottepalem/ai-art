package com.giri.aiart.storage;

import com.giri.aiart.config.MinioConfig;
import com.giri.aiart.config.MinioProperties;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

/// Integration test for {@link MinioStorageService}
///
/// This class verifies upload and download operations against a live MinIO instance
/// using Testcontainers.
///
/// @author Giri Pottepalem
@SpringBootTest(classes = {MinioStorageService.class, MinioConfig.class, MinioProperties.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MinioStorageServiceIntegrationTest {
    /// Setup Testcontainers base class that can run any Docker image (as opposed to specialized ones like
    /// PostgreSQLContainer or MongoDBContainer).
    /// Start a MinIO container dynamically
    ///   * Pull the image from Docker Hub (if not cached)
    ///   * Launch a new, isolated container
    ///   * Expose container’s port 9000 to the host (mapped to a random free port).
    ///      * Container 9000 → Host 52254
    ///      * minio.getMappedPort(9000)
    ///   * In MinIO’s Docker image, set the two env variables required to start:
    ///      * MINIO_ROOT_USER - defines the admin username for accessing the service
    ///      * MINIO_ROOT_PASSWORD - sets the root user’s password inside the container
    ///   * Start a MinIO server that stores its files in /data inside the container.
    ///   * Expose it to your test JVM via a dynamically assigned port
    /// MinIO starts serving on [Minio](http://localhost:52254)
    ///
    static final GenericContainer<?> minio =
            new GenericContainer<>("minio/minio:latest")
                    .withExposedPorts(9000)
                    .withEnv("MINIO_ROOT_USER", "minioadmin")
                    .withEnv("MINIO_ROOT_PASSWORD", "minioadmin")
                    .withCommand("server /data");

    @Autowired
    private MinioStorageService minioStorageService;

    /// Load other minio properties from `application-test.yml`, only the end point is replaced dynamically.
    /// Spring registers these first, then loads application-test.yml, and merges that the service will see
    @DynamicPropertySource
    static void registerMinioProperties(DynamicPropertyRegistry registry) {
        // Start minio to serve at a mapped dynamic port (e.g. http://localhost:52254)
        minio.start();
        // Get host, dynamic port and setup endpoint for the client to connect
        String endpoint = "http://" + minio.getHost() + ":" + minio.getMappedPort(9000);
        registry.add("minio.endpoint", () -> endpoint);
    }

    @AfterAll
    void tearDown() {
        minio.stop();
    }

    @Test
    @DisplayName("Should upload and download text from MinIO successfully")
    void upload_and_download_text_should_succeed() throws Exception {
        // given: a text file for upload
        String objectName = "hello.txt";
        String content = "Hello, MinIO Integration Test!";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(content.getBytes());

        // when: service uploadFile is called
        minioStorageService.uploadFile(objectName, inputStream, "text/plain");

        // then: verify expected result
        String downloadedContent;
        try (InputStream downloaded = minioStorageService.downloadFile(objectName)) {
            downloadedContent = new String(downloaded.readAllBytes(), StandardCharsets.UTF_8);
        }
        assertThat(downloadedContent).isEqualTo(content);
    }

    @Test
    @DisplayName("Should upload and download image from MinIO successfully")
    void upload_and_download_image_should_succeed() throws Exception {
        // given: an input image file for upload
        String objectName = "/images/london-boris.jpg";
        byte[] originalBytes;

        // when: service uploadFile is called
        try (InputStream imageStream = getClass().getResourceAsStream(objectName)) {
            assertThat(imageStream).isNotNull();
            originalBytes = imageStream.readAllBytes();
            var inputStream = new ByteArrayInputStream(originalBytes);
            minioStorageService.uploadFile(objectName, inputStream, "image/jpg");
        }

        // then: verify expected result
        try (InputStream download = minioStorageService.downloadFile(objectName)) {
            byte[] downloadedBytes = download.readAllBytes();
            assertThat(downloadedBytes).hasSameSizeAs(originalBytes);
            assertThat(downloadedBytes).isEqualTo(originalBytes);
            IO.println(downloadedBytes.length);
        }

    }
}
