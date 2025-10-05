package com.giri.aiart.media;

import com.giri.aiart.prompt.PromptType;
import reactor.core.publisher.Flux;

import java.io.IOException;

/// Defines interface for Media Service
/// @author Giri Pottepalem
public interface MediaService {
    /// Given a media filename found in the classpath and a prompt type, it generates a prompt for the AI
    /// and returns the response.
    /// @param mediaFileName the media filename
    /// @param promptType the prompt type
    /// @return response stream
    Flux<String> analyzeMedia(String mediaFileName, PromptType promptType) throws IOException;

    /// Given a media filename found in the classpath that contains text, it generates a prompt for the AI to
    /// extract text from it and returns the text stream of the extracted text.
    /// @param mediaFileName the media filename
    /// @return response stream
    Flux<String> extractText(String mediaFileName) throws IOException;
}
