package com.giri.aiart.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/// Configures multiple `ChatClient` beans with different models for resilience and flexibility.
/// The primary model is configured via Spring AI auto-configuration, and secondary/tertiary
/// models act as fallbacks in case the primary model fails or is unavailable.
///
/// This setup enables dynamic model routing and fallback strategies for multimodal LLM workloads.
///
/// @author Giri Pottepalem
@Configuration
public class ChatClientConfiguration {

    /// Primary ChatClient using the default Ollama model defined in `application.yml`.
    ///
    /// This bean overrides the auto-configured one to allow for flexible extension and
    /// integration with fallback models. The primary model name is resolved from
    /// `spring.ai.ollama.chat.options.model`.
    ///
    /// @param ollamaChatModel the default Ollama chat model
    /// @return a ChatClient configured with the primary model
    /// @see <a href="https://www.baeldung.com/spring-ai-configure-multiple-llms">Configuring Multiple LLMs in Spring AI</a>
    /// See Also: [Configuring Multiple LLMs in Spring AI](https://www.baeldung.com/spring-ai-configure-multiple-llms)
    @Bean
    @Primary
    public ChatClient primaryChatClient(OllamaChatModel ollamaChatModel) {
        return ChatClient.create(ollamaChatModel);
    }

    /// Secondary ChatClient using a fallback Ollama model.
    ///
    /// This client serves as a secondary option when the primary model encounters errors
    /// or is unsuitable for specific tasks.
    ///
    /// @param ollamaApi the Ollama API client
    /// @param ollamaChatModel the base Ollama chat model
    /// @param secondaryModelName the name of the secondary model defined in configuration
    /// @return a ChatClient configured with the secondary model
    @Bean
    public ChatClient secondaryChatClient(
        OllamaApi ollamaApi,
        OllamaChatModel ollamaChatModel,
        @Value("${spring.ai.ollama.chat.options.secondary-model}") String secondaryModelName) {
        OllamaChatOptions ollamaChatOptions = ollamaChatModel.getDefaultOptions().copy();
        ollamaChatOptions.setModel(secondaryModelName);

        var chatModel = OllamaChatModel.builder()
            .ollamaApi(ollamaApi)
            .defaultOptions(ollamaChatOptions)
            .build();
        return ChatClient.create(chatModel);
    }

    /// Tertiary ChatClient for additional fallback or experimental models.
    ///
    /// This model can be used for testing alternative configurations or specialized
    /// multimodal tasks where different model strengths are desired.
    ///
    /// @param ollamaApi the Ollama API client
    /// @param ollamaChatModel the base Ollama chat model
    /// @param tertiaryModelName the name of the tertiary model defined in configuration
    /// @return a ChatClient configured with the tertiary model
    @Bean
    public ChatClient tertiaryChatClient(
        OllamaApi ollamaApi,
        OllamaChatModel ollamaChatModel,
        @Value("${spring.ai.ollama.chat.options.tertiary-model}") String tertiaryModelName) {
        OllamaChatOptions ollamaChatOptions = ollamaChatModel.getDefaultOptions().copy();
        ollamaChatOptions.setModel(tertiaryModelName);

        var chatModel = OllamaChatModel.builder()
            .ollamaApi(ollamaApi)
            .defaultOptions(ollamaChatOptions)
            .build();
        return ChatClient.create(chatModel);
    }
}
