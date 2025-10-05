package com.giri.aiart.prompt;

import org.springframework.ai.chat.prompt.Prompt;

import java.util.List;
import java.util.Map;

/// Prompt Factory to build Prompt with system and user messages and parameters
/// @author Giri Pottepalem
public class PromptFactory {
    /// Builds prompt with default system and user parameters
    /// @param promptType the prompt type
    /// @param mediaFileName media filename sent to multimodal AI
    public static Prompt createPrompt(PromptType promptType, String mediaFileName) {
        return createPrompt(promptType, mediaFileName, PromptBuilder.DEFAULT_PARAMS, PromptBuilder.DEFAULT_PARAMS);
    }

    /// Builds  prompt
    /// @param promptType prompt type
    /// @param mediaFilename the media filename
    /// @param systemParameters system parameters if any
    /// @param userParameters user parameters if any
    /// @return the prompt with system and user messages initialized with parameters
    public static Prompt createPrompt(PromptType promptType, String mediaFilename,
                                      Map<String, Object> systemParameters,
                                      Map<String, Object> userParameters) {
        PromptBuilder promptBuilder = buildFor(promptType);
        return new Prompt(
                List.of(
                    promptBuilder.buildSystemMessage(systemParameters),
                    promptBuilder.buildUserMessage(userParameters, mediaFilename)
                )
        );
    }

    /// Helper, returns the builder for the given prompt type
    /// @param promptType the prompt type
    /// @return the prompt builder
    private static PromptBuilder buildFor(PromptType promptType) {
        return switch (promptType) {
            case DESCRIPTION -> new DescriptionPromptBuilder();
            case CAPTION -> new CaptionPromptBuilder();
        };
    }
}
