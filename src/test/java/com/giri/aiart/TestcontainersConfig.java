package com.giri.aiart;

import com.giri.aiart.shared.util.LogIcons;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
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
@TestConfiguration
@Slf4j
public class TestcontainersConfig {
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
}
