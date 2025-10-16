package com.giri.aiart.modules.embeddings;

import com.giri.aiart.media.MediaService;
import com.giri.aiart.prompt.PromptType;
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

/// Service responsible for generating embeddings for various data types (images, text, etc.)
///
/// @author Giri Pottepalem
@Slf4j
@Service
@RequiredArgsConstructor
public class EmbeddingGeneratorService {
    private static final int DEFAULT_DIMENSIONS = 1536; // typical for image/text embeddings

    private final MediaService mediaService;
    private final EmbeddingModel embeddingModel;

    /// For a given image resource and embedding type, it generates description, and from the description it generates
    /// embeddings.
    /// @param imageResource the image resource
    /// @param embeddingType the embedding type
    /// @return ArtworkEmbedding set with embeddings
    public ArtworkEmbedding generateEmbedding(Resource imageResource, EmbeddingType embeddingType) throws IOException {
        log.info("{} Generating random embedding for {}", LogIcons.TIMER, embeddingType);

        // Use a multimodal LLM to describe the artwork
        String description = mediaService.analyzeMedia(imageResource, PromptType.DESCRIPTION);
        float[] vector = embeddingModel.embed(description); // TODO EmbeddingOptions for dimension 1536

        return ArtworkEmbedding.builder()
            .type(embeddingType)
            .status(EmbeddingStatusType.ACTIVE)
            .embedding(vector)
            .build();
    }

    /// Generates random embeddings
    /// @param embeddingType the embedding type
    /// @return ArtworkEmbedding set with embeddings
    public ArtworkEmbedding generateRandomEmbedding(EmbeddingType embeddingType) {
        float[] vector = EmbeddingUtils.generateRandomEmbedding(DEFAULT_DIMENSIONS);
        ArtworkEmbedding artworkEmbedding = ArtworkEmbedding.builder()
            .type(embeddingType)
            .status(EmbeddingStatusType.ACTIVE)
            .embedding(vector)
            .build();

        log.info("{} Generated {} embedding ({} dimensions)", LogIcons.SUCCESS, embeddingType, vector.length);

        return artworkEmbedding;
    }
}
