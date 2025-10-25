package com.giri.aiart.media;

import com.giri.aiart.prompt.PromptFactory;
import com.giri.aiart.prompt.PromptType;
import com.giri.aiart.shared.util.ChatClientWithMeta;
import com.giri.aiart.shared.util.LogIcons;
import com.giri.aiart.shared.util.MediaUtils;
import com.giri.aiart.shared.util.ModelUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.core.io.Resource;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.List;

/// A concrete implementation of {@link MediaService}
///
/// @See {@link com.giri.aiart.config.ChatClientConfiguration} for {@link ChatClient}
///
/// @author Giri Pottepalem
@Slf4j
@AllArgsConstructor
@Service
public class MediaServiceImpl implements MediaService {
    private final ChatClientWithMeta chatClientWithMeta;
    private final ChatClientWithMeta secondaryChatClient;
    private final ChatClientWithMeta tertiaryChatClient;
    private final SimpleLoggerAdvisor simpleLoggerAdvisor;

    @Override
    public Flux<String> analyzeMediaStreaming(String mediaFileName, PromptType promptType) throws IOException {
        log.info("{} Analyzing mediaFile:{} for prompt: {}", LogIcons.ANALYSIS, mediaFileName, promptType.name());
        ModelUtils.logModelName(chatClientWithMeta.modelName());

        Prompt prompt = PromptFactory.createPrompt(promptType, mediaFileName);

        return chatClientWithMeta.chatClient().prompt(prompt)
            .advisors(simpleLoggerAdvisor)
            .stream()
            .content();
    }

    @Override
    public Flux<String> analyzeMediaStreaming(Resource mediaResource, PromptType promptType) throws IOException {
        log.info("{} Analyzing mediaResource for prompt: {}", LogIcons.ANALYSIS, promptType.name());
        ModelUtils.logModelName(chatClientWithMeta.modelName());

        Prompt prompt = PromptFactory.createPrompt(promptType, mediaResource);
        return chatClientWithMeta.chatClient().prompt(prompt)
            .advisors(simpleLoggerAdvisor)
            .stream()
            .content();
    }

    @Override
    @Retryable(retryFor = {Exception.class}, maxAttempts = 3)
    public String analyzeMedia(Resource mediaResource, PromptType promptType) throws IOException {
        log.info("{} Analyzing mediaResource for prompt: {}", LogIcons.ANALYSIS, promptType.name());
        ModelUtils.logModelName(chatClientWithMeta.modelName());

        Prompt prompt = PromptFactory.createPrompt(promptType, mediaResource);
        return chatClientWithMeta.chatClient().prompt(prompt)
            .advisors(simpleLoggerAdvisor)
            .call()
            .content();
    }

    @Recover
    public String recoverAnalyzeMedia(Exception exception, Resource mediaResource, PromptType promptType) {
        log.warn("{} Primary LLM failure, error : {}", LogIcons.WARNING, exception.getMessage());
        log.info("{} Attempting to process prompt '{}' with secondary LLM", LogIcons.AI, promptType);
        ModelUtils.logModelName(secondaryChatClient.modelName());

        try {
            Prompt prompt = PromptFactory.createPrompt(promptType, mediaResource);
            return secondaryChatClient.chatClient().prompt(prompt)
                .advisors(simpleLoggerAdvisor)
                .call()
                .content();
        } catch (Exception ex) {
            log.warn("{} Secondary LLM failure, error : {}", LogIcons.WARNING, ex.getMessage());
            log.info("{} Attempting to process prompt '{}' with tertiary LLM", LogIcons.AI, promptType);
            ModelUtils.logModelName(tertiaryChatClient.modelName());

            Prompt prompt = PromptFactory.createPrompt(promptType, mediaResource);
            return tertiaryChatClient.chatClient().prompt(prompt)
                .advisors(simpleLoggerAdvisor)
                .call()
                .content();
        }
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
        return chatClientWithMeta.chatClient().prompt(prompt)
            .advisors(simpleLoggerAdvisor)
            .stream()
            .content();
    }
}
