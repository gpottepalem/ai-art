package com.giri.aiart;

import com.giri.aiart.config.MinioProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.modulith.Modulith;

/// The main Spring Boot application.
/// @author Giri Pottepalem
@SpringBootApplication
@Modulith
@EnableJpaAuditing
@EnableConfigurationProperties(MinioProperties.class)
@Slf4j
public class AiArtApplication {
	public static void main(String[] args) {
		SpringApplication.run(AiArtApplication.class, args);
	}

    @Bean
    public SimpleLoggerAdvisor simpleLoggerAdvisor() {
        return new SimpleLoggerAdvisor();
    }

    @Bean
    public CommandLineRunner initializationChecks(@Autowired(required = false)JdbcClient jdbcClient) {
        return args -> {
            if (jdbcClient != null) {
                log.info("Database version: {}", jdbcClient.sql("SELECT version()").query(String.class).single());
            }
        };
    }
}
