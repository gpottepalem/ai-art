package com.giri.aiart.shared.dto;

import com.giri.aiart.shared.domain.Artwork;
import com.giri.aiart.shared.domain.type.ArtType;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

/// DTO for {@link Artwork} entity — includes selected attributes and embedding summary
///
/// @author Giri Pottepalem
public record ArtworkDTO(UUID id,
                         String title,
                         String description,
                         ArtType artType,
                         String minioKey,
                         UUID artistId,
                         List<ArtworkEmbeddingDTO> embeddings) {
    public static ArtworkDTO fromEntity(@NonNull Artwork artwork) {
        return new ArtworkDTO(
            artwork.getId(),
            artwork.getTitle(),
            artwork.getDescription(),
            artwork.getArtType(),
            artwork.getMinioKey(),
            artwork.getArtist() != null ? artwork.getArtist().getId() : null,
            artwork.getEmbeddings() != null
                ? artwork.getEmbeddings().stream()
                    .map(ArtworkEmbeddingDTO::fromEntity)
                    .toList()
                : List.of()
        );
    }
}
