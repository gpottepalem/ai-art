package com.giri.aiart.ollama;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class OllamaServiceImpl implements OllamaService {
    private ChatClient chatClient;
    private OllamaChatModel ollamaChatModel;
    private VectorStore vectorStore;
    private ArtRepository artRepository;

    private final Map<String, PromptChatMemoryAdvisor> memory =  new ConcurrentHashMap<>();

    /// Constructs an instance
    ///
    /// @param chatClientBuilder
    /// @param artRepository
    /// @param vectorStore
    /// @param ollamaChatModel
    public OllamaServiceImpl(
        ChatClient.Builder chatClientBuilder,
        ArtRepository artRepository,
        VectorStore vectorStore,
        // McpSyncClient mcpSyncClient,
        OllamaChatModel ollamaChatModel) {
        var system = """
                You are an AI powered assistant to help people buy an Art work from the Art agency named Art Palace
                with locations in Canton, Boston, New York, Tokyo, Singapore, Paris, Mumbai, New Delhi, Hyderabad, and
                London. Information about the arts available will be presented below.
                If there is no information, then return a polite response suggesting we don't have any arts available.
                """;

        var inMemoryChatRepository = new InMemoryChatMemoryRepository();
        var chatMemory = MessageWindowChatMemory
            .builder()
            .chatMemoryRepository(inMemoryChatRepository)
            .build();

        this.chatClient = chatClientBuilder
            .defaultSystem(system)
            .defaultAdvisors(
                MessageChatMemoryAdvisor.builder(chatMemory).build(),
                new SimpleLoggerAdvisor() // capture and log request and response to and from the LLM
            )
            .build();

        this.vectorStore = vectorStore;
        this.artRepository = artRepository;
        this.ollamaChatModel = ollamaChatModel;
    }

    // TODO - With system prompt/recipe
    @Override
    public Flux<String> generateTextStream(String message) {
        var prompt = new Prompt(new UserMessage(message));
        return ollamaChatModel.stream(prompt)
            .mapNotNull(chatResponse -> chatResponse.getResults().getFirst().getOutput().getText());
    }

    /// LLMs don't output JSON unless you strongly prompt them to do so.
    /// @param message
    /// @return
    @Override
    public Flux<String> generateJsonStream(String message) {
        var systemMessage = new SystemMessage("""
                You are a strict JSON-only assistant.
                Always respond ONLY in valid JSON format.
                Do not include explanations, markdown, or code fences.
                For example: { "answer": "your answer here" }
                """);
        var userMessage = new UserMessage(message);

        var prompt = new Prompt(List.of(systemMessage, userMessage));
        return ollamaChatModel.stream(prompt)
            .mapNotNull(response -> response.getResults().getFirst().getOutput().getText());
//                .collect(Collectors.joining())
//                .map(String::trim);
    }

    /// Advisors - enhance request/response prompt flow, interpret request/response apply filters,
    /// Simplify tasks such as logging, message transformations, and chat memory management,
    /// Chat Memory: [[chat memory](https://docs.spring.io/spring-ai/reference/api/chat-memory.html)]
    /// @param user the user
    /// @param message the message
    @Override
    public Flux<String> assist(String user, String message) {
        var inMemoryChatRepository = new InMemoryChatMemoryRepository();
        var chatMemory = MessageWindowChatMemory
            .builder()
            .chatMemoryRepository(inMemoryChatRepository)
            .build();
        var advisor = PromptChatMemoryAdvisor
            .builder(chatMemory)
            .build();
        var advisorForUser = this.memory.computeIfAbsent(user, key -> advisor);
        var prompt = new Prompt(new UserMessage(message));

        return ollamaChatModel.stream(prompt)
            .mapNotNull(chatResponse -> chatResponse.getResults().getFirst().getOutput().getText());
    }
}
