package com.giri.aiart.chat;

import com.giri.aiart.domain.Painting;
import com.giri.aiart.domain.type.ArtMedia;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;

import java.util.List;

/// Defines interface for service that leverages {@link org.springframework.ai.chat.client.ChatClient}
/// @author Giri Pottepalem
public interface ArtMasterService {
    /// A simple about request response chat with AI
    /// @param artMedia art media
    /// @param object the object
    /// @return chat response
    String artChatContent(ArtMedia artMedia, String object);

    /// A simple about request response chat with AI which returns the actual chat response with all details
    /// @param object the object
    /// @return the actual chat response from AI with all details including token usages
    ChatResponse artChatResponse(ArtMedia artMedia, String object);

    /// A simple about request response chat with AI which returns the actual chat response with all details
    /// and ChatClient execution context
    /// @param object the object
    /// @return the actual chat response from AI with all details including token usages
    ChatClientResponse artChatClientResponse(ArtMedia artMedia, String object);

    /// A simple about request streaming response chat with AI which returns the actual chat response with all details
    /// @param object the object
    /// @return the actual streaming chat response from AI with all details including token usages
    Flux<ChatResponse> artStreamChatResponse(ArtMedia artMedia, String object);

    /// A simple about request streaming response chat with AI
    /// @param object the object
    /// @return streaming response content
    Flux<String> artStreamChatContent(ArtMedia artMedia, String object);

    /// A simple about request streaming words response chat with AI
    /// @param object the object
    /// @return streaming response content word by word
    Flux<String> artStreamChatContentWords(ArtMedia artMedia, String object);

    /// A simple about request returning {@link Painting} entity
    /// @param about chat about
    /// @return {@link Painting} entity
    List<Painting> synchronousChatEntity(String about);

    /// A simple about request returning stream of List of {@link Painting} entities
    /// @param about chat about
    /// @return {@link Flux<List<Painting>>} entities
    Flux<List<Painting>> streamingEntityCollection(String about);
}
