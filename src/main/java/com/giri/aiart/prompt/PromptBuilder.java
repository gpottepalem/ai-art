package com.giri.aiart.prompt;

import lombok.NonNull;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.HashMap;
import java.util.Map;

/// Defines interface for a generic prompt builder.
/// @author Giri Pottepalem
public interface PromptBuilder {
    Map<String, Object> DEFAULT_PARAMS = Map.of(
    "maxWords", 8,
    "style", "expert"
    );

    /// Given a map of parameters, it builds and returns system message
    SystemMessage buildSystemMessage(Map<String, Object> parameters);

    /// Given a map of parameters, it builds and returns user message
    /// @param parameters user message parameters
    /// @param mediaFilename the media filename to be used for multimodal AI
    UserMessage buildUserMessage(Map<String, Object> parameters, String mediaFilename);

    /// Helper/default method to merge user parameters with default parameters
    /// @param userParams user parameters
    /// @return the merged parameters
    default Map<String, Object> mergeParameters(@NonNull Map<String, Object> userParams) {
        Map<String, Object> merged = new HashMap<>(DEFAULT_PARAMS);
        merged.putAll(userParams);
        return merged;
    }
}
