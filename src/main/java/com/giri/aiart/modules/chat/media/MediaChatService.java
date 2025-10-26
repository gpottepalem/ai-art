package com.giri.aiart.modules.chat.media;

import com.giri.aiart.prompt.PromptType;
import org.springframework.core.io.Resource;
import reactor.core.publisher.Flux;

import java.io.IOException;

/// Provides interface for AI based media analysis. In other words, provides core contract for AI-powered
/// **media understanding and analysis** within the AI-Art platform.
///
/// The `MediaService` abstracts the interaction between the application layer and multimodal AI models
/// (such as LLaVA, BakLLaVA, Gemini, or CLIP) to perform **vision-language inference** —
/// enabling the extraction of meaningful textual representations or structured data from visual media.
///
/// Responsibilities:
/// - Generates **semantic descriptions**, **captions**, or **contextual prompts** from images and videos.
/// - Streams AI model responses for real-time or progressive UIs.
/// - Supports both **reactive (Flux)** and **blocking** consumption patterns for flexibility.
/// - Serves as the bridge between media ingestion (artworks, references) and AI-based reasoning.
///
/// Typical use cases:
/// - Generating textual prompts or summaries for artworks.
/// - Extracting text (OCR-like) from image media.
/// - Feeding descriptions into `EmbeddingModel` for vectorization.
///
/// Implementations may use Spring AI’s `ChatModel` or `MultimodalModel` abstractions underneath,
/// allowing dynamic provider selection (e.g., Ollama, OpenAI, Vertex AI) at runtime.
///
/// @see com.giri.aiart.prompt.PromptType
///
public interface MediaChatService {
    /// Analyzes a media file (located on the classpath) using the specified prompt type
    /// and returns a **streaming response** from the AI model.
    ///
    /// This variant is useful for small bundled assets or internal demo media.
    /// Streaming enables partial results to be consumed as the model generates them.
    ///
    /// @param mediaFileName the name of the media file found in the classpath
    /// @param promptType the prompt type determining the nature of the AI analysis
    /// @return a reactive `Flux<String>` representing the streamed AI response
    /// @throws IOException if the media resource cannot be read
    Flux<String> analyzeMediaStreaming(String mediaFileName, PromptType promptType) throws IOException;

    /// Analyzes a given media `Resource` using the specified prompt type
    /// and returns a **streaming response** from the AI model.
    ///
    /// This variant supports any Spring `Resource` — including remote URLs, file uploads,
    /// and storage-backed content — and provides real-time AI output streaming.
    ///
    /// @param mediaResource the Spring `Resource` pointing to the image, video, or media file
    /// @param promptType the prompt type determining how the AI should interpret the media
    /// @return a reactive `Flux<String>` representing the streamed AI response
    /// @throws IOException if reading the media resource fails
    Flux<String> analyzeMediaStreaming(Resource mediaResource, PromptType promptType) throws IOException;

    /// Analyzes a given media `Resource` using the specified prompt type and
    /// returns the **full non-streaming AI response** as a single concatenated string.
    ///
    /// This blocking variant is suitable for synchronous processing or embedding generation workflows
    /// (e.g., `EmbeddingGeneratorService`).
    ///
    /// @param mediaResource the media resource
    /// @param promptType the prompt type
    /// @return a single aggregated AI response string
    /// @throws IOException if reading the media resource fails
    String analyzeMedia(Resource mediaResource, PromptType promptType) throws IOException;

    /// Extracts textual content (OCR or caption text) from a classpath media file
    /// using an AI model capable of text recognition or visual question answering.
    ///
    /// The output is streamed progressively as the model processes the media.
    ///
    /// @param mediaFileName the name of the media file in the classpath
    /// @return a reactive `Flux<String>` emitting lines of extracted text
    /// @throws IOException if the resource cannot be loaded
    Flux<String> extractText(String mediaFileName) throws IOException;

    // -------------------------------------
    // 🌟 Semantic Convenience Methods
    // -------------------------------------

    /// Generates a descriptive caption for the given image resource using AI.
    /// This is a shorthand for analyzeMedia(..., PromptType.DESCRIPTION).
    default String describeImage(Resource imageResource) throws IOException {
        return analyzeMedia(imageResource, PromptType.DESCRIPTION);
    }

    /// Generates a caption caption for the given image resource using AI.
    /// This is a shorthand for analyzeMedia(..., PromptType.CAPTION).
    default String captionImage(Resource imageResource) throws IOException {
        return analyzeMedia(imageResource, PromptType.CAPTION);
    }

    /// Streams an AI-generated description for the given image resource.
    /// This is a shorthand for analyzeMediaStreaming(..., PromptType.DESCRIPTION).
    default Flux<String> streamImageDescription(Resource imageResource) throws IOException {
        return analyzeMediaStreaming(imageResource, PromptType.DESCRIPTION);
    }

    /// Streams an AI-generated description for the given image resource.
    /// This is a shorthand for analyzeMediaStreaming(..., PromptType.CAPTION).
    default Flux<String> streamImageCaption(Resource imageResource) throws IOException {
        return analyzeMediaStreaming(imageResource, PromptType.CAPTION);
    }
}
