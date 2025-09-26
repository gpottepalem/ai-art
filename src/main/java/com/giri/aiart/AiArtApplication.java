package com.giri.aiart;

import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AiArtApplication {
	public static void main(String[] args) {
		SpringApplication.run(AiArtApplication.class, args);
	}

    @Bean
    public SimpleLoggerAdvisor simpleLoggerAdvisor() {
        return new SimpleLoggerAdvisor();
    }
}
