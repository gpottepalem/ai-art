package com.giri.aiart.prompt;

import com.giri.aiart.util.MediaUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.Map;

/// A concrete implementation of {@link PromptBuilder}
/// @author Giri Pottepalem
@Slf4j
public class DescriptionPromptBuilder implements PromptBuilder {
    public static final Map<String, Object> DEFAULT_PARAMS = Map.of();

    @Override
    public SystemMessage buildSystemMessage(@NonNull Map<String, Object> parameters) {
        int maxWords = (int) parameters.getOrDefault("maxWords", 100);
        String systemPrompt = String.format("""
                You are a description expert.
                Be as descriptive as you can in about 3 paras and never exceeding %d words.
                """, maxWords);
        return SystemMessage.builder()
                .text(systemPrompt)
                .build();
    }

    @Override
    public UserMessage buildUserMessage(@NonNull Map<String, Object> parameters, String mediaFilename) {
        return new UserMessage.Builder()
//                .text("Describe this image.")
                .text("Write a story describing this image.")
                .media(MediaUtils.toMedia(mediaFilename))
                .build();
    }
}
