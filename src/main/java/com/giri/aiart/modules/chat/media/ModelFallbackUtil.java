package com.giri.aiart.modules.chat.media;

import com.giri.aiart.prompt.PromptFactory;
import com.giri.aiart.prompt.PromptType;
import com.giri.aiart.shared.util.ChatClientWithMeta;
import com.giri.aiart.shared.util.LogIcons;
import com.giri.aiart.shared.util.ModelUtils;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.core.io.Resource;
import reactor.core.publisher.Flux;

/// Model fallback utility class
/// Provides generic methods (non-streaming and streaming) for fallback models that:
///  - Execute a prompt across secondary → tertiary models as fallback in case of failure.
///  - If secondary model fails, automatically try tertiary model.
///  - Log progress and returns the successful response.
///
/// @author Giri Pottepalem
@Slf4j
@UtilityClass
public class ModelFallbackUtil {
    /// Fallback for non-streaming use-cases (String-based responses)
    public String executeFallback(
        Exception primaryException,
        PromptType promptType,
        Resource mediaResource,
        SimpleLoggerAdvisor advisor,
        ChatClientWithMeta secondary,
        ChatClientWithMeta tertiary
    ) {
        log.warn("{} Primary LLM failed: {}", LogIcons.WARNING, primaryException.getMessage());
        log.info("{} Retrying with secondary model...", LogIcons.AI);
        ModelUtils.logModelName(secondary.modelName());

        try {
            Prompt prompt = PromptFactory.createPrompt(promptType, mediaResource);
            return secondary.chatClient().prompt(prompt)
                .advisors(advisor)
                .call()
                .content();
        } catch (Exception ex2) {
            log.warn("{} Secondary LLM failed: {}", LogIcons.WARNING, ex2.getMessage());
            log.info("{} Retrying with tertiary model...", LogIcons.AI);
            ModelUtils.logModelName(tertiary.modelName());

            Prompt prompt = PromptFactory.createPrompt(promptType, mediaResource);
            return tertiary.chatClient().prompt(prompt)
                .advisors(advisor)
                .call()
                .content();
        }
    }

    /// Fallback for streaming use-cases (Flux-based responses)
    public Flux<String> executeFallbackStreaming(
        Exception primaryException,
        PromptType promptType,
        String mediaFileName,
        SimpleLoggerAdvisor advisor,
        ChatClientWithMeta secondary,
        ChatClientWithMeta tertiary
    ) {
        log.warn("{} Primary LLM failed: {}", LogIcons.WARNING, primaryException.getMessage());
        log.info("{} Retrying with secondary model...", LogIcons.AI);
        ModelUtils.logModelName(secondary.modelName());

        try {
            Prompt prompt = PromptFactory.createPrompt(promptType, mediaFileName);
            return secondary.chatClient().prompt(prompt)
                .advisors(advisor)
                .stream()
                .content();
        } catch (Exception ex2) {
            log.warn("{} Secondary LLM failed: {}", LogIcons.WARNING, ex2.getMessage());
            log.info("{} Retrying with tertiary model...", LogIcons.AI);
            ModelUtils.logModelName(tertiary.modelName());

            Prompt prompt = PromptFactory.createPrompt(promptType, mediaFileName);
            return tertiary.chatClient().prompt(prompt)
                .advisors(advisor)
                .stream()
                .content();
        }
    }
}
