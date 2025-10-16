package com.giri.aiart.media;

import com.giri.aiart.prompt.PromptFactory;
import com.giri.aiart.prompt.PromptType;
import com.giri.aiart.shared.util.LogIcons;
import com.giri.aiart.shared.util.MediaUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.List;

/// A concrete implementation of {@link MediaService}
/// @author Giri Pottepalem
@Slf4j
@Service
public class MediaServiceImpl implements MediaService {
    private final ChatClient chatClient;
    private final SimpleLoggerAdvisor simpleLoggerAdvisor;

    MediaServiceImpl(ChatClient.Builder chatClientBuilder, SimpleLoggerAdvisor simpleLoggerAdvisor) {
        this.chatClient = chatClientBuilder.build();
        this.simpleLoggerAdvisor = simpleLoggerAdvisor;
    }

    @Override
    public Flux<String> analyzeMediaStreaming(String mediaFileName, PromptType promptType) throws IOException {
        log.info("{} Analyzing mediaFile:{} for generating {}", LogIcons.ANALYSIS, mediaFileName, promptType.name());
        Prompt prompt = PromptFactory.createPrompt(promptType, mediaFileName);

        return chatClient.prompt(prompt)
            .advisors(simpleLoggerAdvisor)
            .stream()
            .content();
    }

    @Override
    public Flux<String> analyzeMediaStreaming(Resource mediaResource, PromptType promptType) throws IOException {
        log.info("{} Analyzing mediaResource for generating {}", LogIcons.ANALYSIS, promptType.name());
        Prompt prompt = PromptFactory.createPrompt(promptType, mediaResource);
        return chatClient.prompt(prompt)
            .advisors(simpleLoggerAdvisor)
            .stream()
            .content();
    }

    @Override
    public String analyzeMedia(Resource mediaResource, PromptType promptType) throws IOException {
        log.info("{} Analyzing mediaResource for generating {}", LogIcons.ANALYSIS, promptType.name());
        Prompt prompt = PromptFactory.createPrompt(promptType, mediaResource);
        return chatClient.prompt(prompt)
            .advisors(simpleLoggerAdvisor)
            .call()
            .content();
    }

    @Override
    public Flux<String> extractText(String mediaFileName) throws IOException {
        var systemMessage = new SystemMessage("""
                You are a multi-lingual expert in extracting text from image.
                The following is a screenshot containing a quotation.
                """);
        var userMessage = UserMessage.builder()
            .text("Extract all the text found in this image.")
            .media(MediaUtils.toMedia(mediaFileName))
            .build();
        var prompt = new Prompt(List.of(systemMessage, userMessage));
        return chatClient.prompt(prompt)
            .advisors(simpleLoggerAdvisor)
            .stream()
            .content();
    }
}
