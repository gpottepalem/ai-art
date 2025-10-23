package com.giri.aiart.config;

import com.giri.aiart.shared.util.LogIcons;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mockito.Mockito;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/// Test configuration class for setting up containerized services using Testcontainers.
///
/// This class is marked with `@TestConfiguration`, making it active only during test runs.
/// It provides bean definitions for containerized dependencies such as PostgreSQL with the
/// pgvector extension. These containers are managed by Testcontainers and can be injected
/// into test contexts using Spring.
///
/// The use of `@ServiceConnection` indicates that the container should automatically
/// integrate with Spring Boot's service connection abstraction (e.g., for database configuration).
///
/// @see <a href="https://www.testcontainers.org/">Testcontainers Documentation</a>
/// @see <a href="https://hub.docker.com/r/pgvector/pgvector">pgvector Docker image</a>
///
///
/// This test configuration is **REQUIRED** to deal with the issue of Flyway migrations involving the `vector` extension.
///
/// ```
/// Message    : ERROR: extension "vector" is not available
///          - @ConditionalOnClass did not find required class 'com.rabbitmq.client.ConnectionFactory' (OnClassCondition)
///   Hint: The extension must first be installed on the system where PostgreSQL is running.
/// ```
/// ```
/// **NOTE:** Annotate Integration test with: `@Import(TestcontainersConfig.class)`
/// to ensure the containerized PostgreSQL with pgvector is loaded properly.
/// ```
///
/// @author Giri Pottepalem
@Slf4j
@TestConfiguration
@EnableConfigurationProperties(MinioProperties.class)
@AllArgsConstructor
public class TestcontainersConfig {
    private final MinioProperties minioProperties;

    /// Creates and configures a PostgreSQL container with the pgvector extension for integration testing.
    ///
    /// This bean provides a `PostgreSQLContainer` initialized with a Docker image that includes
    /// the pgvector extension. It is intended for use in test environments where vector-based
    /// search functionality (e.g., similarity search) needs to be tested against a real database.
    ///
    /// @return a configured instance of `PostgreSQLContainer` using the pgvector image.
    /// @see <a href="https://hub.docker.com/r/pgvector/pgvector">pgvector Docker image</a>
    @Bean // Spring-managed bean for dependency injection.
    @ServiceConnection // Indicates this container provides a service connection (used with Testcontainers).
    public PostgreSQLContainer<?> pgVectorContainer() {
        log.info("{} Configuring PostgreSQL Container with pgvector extension for testcontainers", LogIcons.STARTUP);
        // Use a specific pgvector image, e.g., for PostgreSQL 18
        var image = DockerImageName.parse("pgvector/pgvector:pg18");
        return new PostgreSQLContainer<>(image);
    }

    @Bean
    public OllamaApi ollamaApi() {
        return Mockito.mock(OllamaApi.class);
    }

    /// A {@link GenericContainer} instance for running a MinIO Docker container configured with:
    /// - The latest docker image
    /// - The default MinIO port (9000) exposed
    /// - Default root credentials
    /// - A command to start the MinIO server.
    public static final GenericContainer<?> MINIO =
        new GenericContainer<>(DockerImageName.parse("minio/minio:latest"))
            .withExposedPorts(9000)
            .withEnv("MINIO_ROOT_USER", "minioadmin")
            .withEnv("MINIO_ROOT_PASSWORD", "minioadmin")
        .withCommand("server /data");

    /*
     * A static initializer block to start the MinIO container and configure the Spring application properties.
     * This block is executed once when the test class is loaded. It starts the container and sets system properties
     * so the Spring environment will pick them up and configure the MinIO client.
     */
    static {
        MINIO.start();
        log.info("{} Started Minio container and configuring with properties...", LogIcons.STARTUP);
        String endpoint = "http://" + MINIO.getHost() + ":" + MINIO.getMappedPort(9000);

        // Set Spring properties via System properties so your config picks them up
        System.setProperty("minio.endpoint", endpoint);
        System.setProperty("minio.access-key", "minioadmin");
        System.setProperty("minio.secret-key", "minioadmin");
        System.setProperty("minio.secure", "false");
        System.setProperty("minio.bucket-name", "test-ai-art");
    }

    // Ollama container for integration tests
//    @Bean(destroyMethod = "stop")
//    public GenericContainer<?> ollamaContainer() {
//        var container = new GenericContainer<>(DockerImageName.parse("ollama/ollama:latest"))
//            .withExposedPorts(11434)
//            .waitingFor(Wait.forListeningPort())
//            .withCommand("serve"); // starts the Ollama server
//        container.start();
//        log.info("{} Started Ollama container on {}:{}", LogIcons.STARTUP,
//            container.getHost(), container.getMappedPort(11434));
//
//        // Set system property for Spring AI to connect to Testcontainers Ollama
//        System.setProperty("spring.ai.ollama.base-url",
//            "http://" + container.getHost() + ":" + container.getMappedPort(11434));
//
//        return container;
//    }
}
