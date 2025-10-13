package com.giri.aiart.chat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.giri.aiart.shared.domain.Painting;
import com.giri.aiart.shared.domain.type.ArtMedia;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

/// A concrete implementation of {@link ArtMasterService}.
/// Leverages {@link ChatClient} API for communicating with the AI Model.
/// [Chat Client API](https://docs.spring.io/spring-ai/reference/api/chatclient.html)
/// **NOTES**
/// . Replace call() with stream() for streaming.
///
/// @author Giri Pottepalem
@Slf4j
@Service
public class ArtMasterServiceImpl implements ArtMasterService {
    private final ChatClient chatClient;
    private final SimpleLoggerAdvisor simpleLoggerAdvisor;
    private final ObjectMapper objectMapper;

    public String generateRecipe(ArtMedia artMedia, String object) {
        return String.format("""
                Generate detailed instructions for making an object: %s in art form: %s and art media: %s.
                Include material list, step-by-step instructions (max 4), time required, and helpful tips (max 4).
                Format it nicely using HTML with clear sections.
                """, object, artMedia.getArtType().getLabel(), artMedia.getLabel());
    }
    /// Constructs and instance of {@link ArtMasterServiceImpl}
    public ArtMasterServiceImpl(ChatClient.Builder builder, SimpleLoggerAdvisor simpleLoggerAdvisor, ObjectMapper objectMapper) {
        this.chatClient = builder.build();
        this.simpleLoggerAdvisor = simpleLoggerAdvisor;
        this.objectMapper = objectMapper;
    }

    @Override
    public String artChatContent(ArtMedia artMedia, String object) {
        return chatClient.prompt(generateRecipe(artMedia, object))
            .advisors(simpleLoggerAdvisor) // log both request and response in DEBUG mode if enabled
            .call()
            .content();
    }

    @Override
    public ChatResponse artChatResponse(ArtMedia artMedia, String object) {
        return chatClient.prompt(generateRecipe(artMedia, object))
            .advisors(simpleLoggerAdvisor)
            .call()
            .chatResponse();
    }

    @Override
    public ChatClientResponse artChatClientResponse(ArtMedia artMedia, String object) {
        return chatClient.prompt(generateRecipe(artMedia, object))
            .advisors(simpleLoggerAdvisor)
            .call()
            .chatClientResponse();
    }
    @Override
    public Flux<ChatResponse> artStreamChatResponse(ArtMedia artMedia, String object) {
        return chatClient.prompt(generateRecipe(artMedia, object))
            .advisors(simpleLoggerAdvisor)
            .stream()
            .chatResponse();
    }

    @Override
    public Flux<String> artStreamChatContent(ArtMedia artMedia, String object) {
        return chatClient.prompt()
            .advisors(simpleLoggerAdvisor)
            .user(generateRecipe(artMedia, object)) // set user input
            .stream() // enable streaming
            .content(); // return only content (no metadata)
    }

    @Override
    public Flux<String> artStreamChatContentWords(ArtMedia artMedia, String object){
        return chatClient.prompt()
            .advisors(simpleLoggerAdvisor)
            .user(generateRecipe(artMedia, object))
            .stream()
            .chatClientResponse()
            .mapNotNull(chatResponse -> {
                assert chatResponse.chatResponse() != null;
                return chatResponse.chatResponse().getResults().getFirst().getOutput().getText();
            });
    }

    @Override
    public List<Painting> synchronousChatEntity(String object) {
        return chatClient.prompt()
            .advisors(simpleLoggerAdvisor) // log both request and response in DEBUG mode if enabled
            .user(object)
            .call()
            .entity(new ParameterizedTypeReference<List<Painting>>() {});
    }

    @Override
    public Flux<List<Painting>> streamingEntityCollection(String object) {
        var outputConverter = new BeanOutputConverter<>(Painting.class);
        /*
        return chatClient.prompt()
                .advisors(simpleLoggerAdvisor)
                .user(object)
                .stream()
                .content()
                // accumulate the chunks into bigger JSON strings
                .bufferUntil(s -> s.trim().endsWith("]"))
                // bufferUntil is one approach: wait until a chunk that ends in "]" — crude but possible
                .map(listOfChunks -> String.join("", listOfChunks))
                .map(json -> {
                    try {
                        return objectMapper.readValue(json, new TypeReference<List<Painting>>() {});
                    } catch (Exception e) {
                        // TODO: handle parse error
                        // maybe return empty list or partial parse
//                        log.error("Error parsing json response", e);
                        log.error("Error parsing json response");
                        return List.<Painting>of();
                    }
                });

        */
        return chatClient.prompt()
            .user(object)
            .stream()
            .content()
            .flatMap(chunk -> {
                try {
                    // Only handle list responses
                    List<Painting> list = objectMapper.readValue(
                        chunk,
                        new TypeReference<List<Painting>>() {}
                    );
                    return Flux.just(list); // emits one List<Painting> per chunk
                } catch (Exception e) {
                    System.err.println("Failed to parse chunk: " + chunk);
                    return Flux.empty(); // skip bad chunks
                }
            });
    }
}
