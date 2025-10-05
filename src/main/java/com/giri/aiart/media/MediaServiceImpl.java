package com.giri.aiart.media;

import com.giri.aiart.prompt.PromptFactory;
import com.giri.aiart.prompt.PromptType;
import com.giri.aiart.util.MediaUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
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
    public Flux<String> analyzeMedia(String mediaFileName, PromptType promptType) throws IOException {
        Prompt prompt = PromptFactory.createPrompt(promptType, mediaFileName);

        return chatClient.prompt(prompt)
                .advisors(simpleLoggerAdvisor)
                .stream()
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
