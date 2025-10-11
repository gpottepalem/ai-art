package com.giri.aiart.storage;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

/// Unit test for `MinioStorageService`
///
/// @author Giri Pottepalem
@Testcontainers // Signals JUnit 5 to enable Testcontainers extension for this specific class
public class MinioStorageServiceUnitTest {
    // Field annotation, Testcontainers extension scans test class for any fields marked with this annotation
    //  and takes over their lifecycle management.
    // Static fields: The container is started once before any test methods in the class are executed and stopped only
    //  after the last test method has finished. This is useful for expensive-to-start containers that all your tests
    //  can share, like a database.
    // Instance fields: A new container is started before each test method and stopped after each test method. This
    //  provides maximum test isolation, but it can be slower due to the overhead of starting a new container for every test.
    @Container
    private static final GenericContainer<?> minio =
        new GenericContainer<>("minio/minio:latest")
            .withExposedPorts(9000)
            .withEnv("MINIO_ROOT_USER", "minioadmin")
            .withEnv("MINIO_ROOT_PASSWORD", "minioadmin")
            .withCommand("server /data");

    @Test
    void minio_should_be_running() {
        // The container is started and stopped by the JUnit 5 extension.
        // No manual start() or try-with-resources needed here.
        var mappedPort = minio.getMappedPort(9000);
        assertThat(mappedPort).isInstanceOf(Integer.class).isGreaterThan(0);
        IO.println("MinIO is available at port: " + mappedPort);
    }
}
