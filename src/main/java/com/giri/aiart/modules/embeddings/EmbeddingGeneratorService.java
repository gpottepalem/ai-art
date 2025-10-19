package com.giri.aiart.modules.embeddings;

import com.giri.aiart.media.MediaService;
import com.giri.aiart.shared.domain.ArtworkEmbedding;
import com.giri.aiart.shared.domain.type.EmbeddingStatusType;
import com.giri.aiart.shared.domain.type.EmbeddingType;
import com.giri.aiart.shared.util.EmbeddingUtils;
import com.giri.aiart.shared.util.LogIcons;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;

/// Service responsible for generating vector embeddings for artworks and related media content.
///
/// This component bridges the **media analysis** and **AI embedding** layers of the AI-Art platform:
/// - It uses a multimodal AI model (via `MediaService`) to derive semantic descriptions from images or other media.
/// - It then transforms these descriptions into numerical vector representations (embeddings) suitable for
///   storage in a vector database or similarity search index.
///
/// The service leverages the **Spring-provided `EmbeddingModel` abstraction**, which delegates embedding generation
/// to the configured AI provider (e.g., Ollama, OpenAI, or local models). This ensures portability across different
/// embedding backends with consistent API semantics.
///
/// Embeddings capture the **semantic meaning** of an artwork — enabling:
/// - Artwork similarity and clustering
/// - Prompt-based retrieval and recommendation
/// - Multimodal (image ↔ text) search
///
/// The service supports both deterministic embeddings (via AI model inference)
/// and synthetic/random embeddings (for testing or placeholder data).
///
/// Typical embedding dimensionality is 1536, consistent with modern foundation models such as CLIP, OpenAI, and LLaVA.
///
/// @author
///     Giri Pottepalem
/// @see com.giri.aiart.shared.domain.ArtworkEmbedding
/// @see com.giri.aiart.media.MediaService
/// @see org.springframework.ai.embedding.EmbeddingModel
///
@Slf4j
@Service
@RequiredArgsConstructor
public class EmbeddingGeneratorService {
    private static final int DEFAULT_DIMENSION = 1536;

    private final MediaService mediaService;
    private final EmbeddingModel embeddingModel;

    /// Generates an `ArtworkEmbedding` for a given image resource by:
    /// 1. Invoking the `MediaService` to generate a semantic description of the image.
    /// 2. Using an AI embedding model to convert the description into a numerical vector.
    ///
    /// @param imageResource  the image or media file for which the embedding should be generated
    /// @param embeddingType  the type/category of embedding (e.g., ARTWORK, STYLE, THEME)
    /// @return an `ArtworkEmbedding` containing the generated vector and associated metadata
    /// @throws IOException if reading the image resource fails
    public ArtworkEmbedding generateEmbedding(Resource imageResource, EmbeddingType embeddingType) throws IOException {
        log.info("{} Generating random embedding for {}", LogIcons.TIMER, embeddingType);

        // Use a multimodal LLM to describe the artwork
        String description = mediaService.describeImage(imageResource);
        float[] vector = embeddingModel.embed(description); // TODO EmbeddingOptions for dimension 1536

        return ArtworkEmbedding.builder()
            .type(embeddingType)
            .status(EmbeddingStatusType.ACTIVE)
            .embedding(vector)
            .build();
    }

    /// Generates a random embedding for testing or simulation purposes.
    ///
    /// This method produces a deterministic-dimension random vector (using uniform distribution), which can be used
    /// when no real embedding model inference is required — for example, during integration tests or prototype
    /// deployments.
    ///
    /// @param embeddingType the embedding type (e.g., ARTWORK, THEME)
    /// @return an `ArtworkEmbedding` initialized with a randomly generated vector
    public ArtworkEmbedding generateRandomEmbedding(EmbeddingType embeddingType) {
        float[] vector = EmbeddingUtils.generateRandomEmbedding(DEFAULT_DIMENSION);
        ArtworkEmbedding artworkEmbedding = ArtworkEmbedding.builder()
            .type(embeddingType)
            .status(EmbeddingStatusType.ACTIVE)
            .embedding(vector)
            .build();

        log.info("{} Generated {} embedding ({} dimensions)", LogIcons.SUCCESS, embeddingType, vector.length);

        return artworkEmbedding;
    }
}
