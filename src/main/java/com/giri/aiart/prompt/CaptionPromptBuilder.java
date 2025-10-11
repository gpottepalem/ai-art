package com.giri.aiart.prompt;

import com.giri.aiart.util.MediaUtils;
import lombok.NonNull;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.Map;

/// A concrete implementation of {@link PromptBuilder}
/// @author Giri Pottepalem
public class CaptionPromptBuilder implements PromptBuilder {

    @Override
    public SystemMessage buildSystemMessage(@NonNull Map<String, Object> parameters) {
        int maxWords = (int) parameters.getOrDefault("maxWords", 8);
        String systemPrompt = String.format("""
                You are a caption generator expert.
                Generate only one caption that is short and sweet.
                The caption must never exceed %d words.
                """, maxWords);
        return SystemMessage.builder()
            .text(systemPrompt)
            .build();
    }

    @Override
    public UserMessage buildUserMessage(Map<String, Object> parameters, String mediaFilename) {
        return new UserMessage.Builder()
            .text("Generate a caption for this image.")
            .media(MediaUtils.toMedia(mediaFilename))
            .build();
    }
}
