package com.giri.aiart.shared.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;

/// Utility class providing helper methods for introspecting AI model metadata
/// such as model names and configuration details used by Spring AI components.
///
/// Supports {@link OllamaChatModel} and {@link OllamaEmbeddingModel}. Returns `"unknown-model"`
/// for unsupported types.
///
/// @author Giri Pottepalem
@Slf4j
@UtilityClass
public class ModelUtils {
    /// Returns the model name used by a given Spring AI {@link ChatClient} instance.
    /// @param chatClient the AI chatClient instance
    /// @deprecated - unable to get chatModel from chatClient
    /// @see com.giri.aiart.config.ChatClientConfiguration
    ///
    /// @return the model name if resolvable, otherwise `"unknown-model"`
    public String getModelNameFromChatClient(@NonNull ChatClient chatClient) {
        try {
            // Access private `chatModel` field from ChatClient using reflection
            var field = ChatClient.class.getDeclaredField("chatModel");
            field.setAccessible(true);
            Object chatModel = field.get(chatClient);
            return getModelNameFromChatModel(chatModel);
        } catch (Exception e) {
            log.error("{} Exception finding model name from chatClient...", LogIcons.ERROR, e);
            return "unknown-model";
        }
    }

    /// Returns the model name used by a given Spring AI model instance.
    /// @param model the AI model instance (e.g., OllamaChatModel or OllamaEmbeddingModel)
    /// @return the model name if resolvable, otherwise `"unknown-model"`
    ///
    /// @deprecated - unable to get chatModel from chatClient
    /// @see com.giri.aiart.config.ChatClientConfiguration
    public String getModelNameFromChatModel(@NonNull Object model) {
        return switch (model) {
            case OllamaChatModel chatModel ->
                chatModel.getDefaultOptions() != null
                    ? chatModel.getDefaultOptions().getModel()
                    : "unknown-model";

            case OllamaEmbeddingModel embeddingModel ->{
                try {
                    // OllamaEmbeddingModel exposes OllamaApi through a getter
                    var api = (java.lang.reflect.Field) embeddingModel
                        .getClass()
                        .getDeclaredField("ollamaApi");
                    api.setAccessible(true);
                    var ollamaApi = api.get(embeddingModel);
                    String modelName = (String) embeddingModel
                        .getClass()
                        .getMethod("getModel")
                        .invoke(embeddingModel);
                    yield modelName != null ? modelName : "unknown-model";
                } catch (Exception e) {
                    log.error("{} Exception finding model name from chatModel...", LogIcons.ERROR, e);
                    yield "unknown-model";
                }
            }

            case null, default -> "unknown-model";
        };
    }

    /// Logs the model name with a consistent format.
    /// @param chatClient the AI ChatClient instance
    public void logModelName(ChatClient chatClient) {
        log.info("{} Using model: {}...", LogIcons.OLLAMA, getModelNameFromChatClient(chatClient));
    }

    /// Logs the model name with a consistent format.
    /// @param modelName the AI modelName
    public void logModelName(@NonNull String modelName) {
        log.info("{} Using model: {}...", LogIcons.OLLAMA, modelName);
    }
}
