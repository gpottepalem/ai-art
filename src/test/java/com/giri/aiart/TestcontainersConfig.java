package com.giri.aiart;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/// This test configuration is REQUIRED to deal with the issue of Flyway runs with vector extension
/// ```
/// Message    : ERROR: extension "vector" is not available
///          - @ConditionalOnClass did not find required class 'com.rabbitmq.client.ConnectionFactory' (OnClassCondition)
///   Hint: The extension must first be installed on the system where PostgreSQL is running.
/// ```
/// ```
/// **NOTE:** Annotate Integration test with: @Import(TestcontainersConfig.class)
/// ```
///
/// @author Giri Pottepalem
@TestConfiguration
public class TestcontainersConfig {
    @Bean
    @ServiceConnection
    public PostgreSQLContainer<?> pgVectorContainer() {
        // Use a specific pgvector image, e.g., for PostgreSQL 18
        return new PostgreSQLContainer<>(DockerImageName.parse("pgvector/pgvector:pg18"));
    }
}
