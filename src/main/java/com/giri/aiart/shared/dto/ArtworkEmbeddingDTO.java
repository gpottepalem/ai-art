package com.giri.aiart.shared.dto;

import com.giri.aiart.shared.domain.ArtworkEmbedding;
import com.giri.aiart.shared.domain.type.EmbeddingStatusType;
import com.giri.aiart.shared.domain.type.EmbeddingType;
import lombok.NonNull;

import java.util.UUID;

/// DTO for ArtworkEmbedding — omits full Artwork reference
///
/// @author Giri Pottepalem
public record ArtworkEmbeddingDTO(UUID id, EmbeddingType type, EmbeddingStatusType status, float[] embedding) {
    /// Create DTO from Domain object
    public static ArtworkEmbeddingDTO fromEntity(@NonNull ArtworkEmbedding embedding) {
        return new ArtworkEmbeddingDTO(
            embedding.getId(),
            embedding.getType(),
            embedding.getStatus(),
            embedding.getEmbedding()
        );
    }
}
