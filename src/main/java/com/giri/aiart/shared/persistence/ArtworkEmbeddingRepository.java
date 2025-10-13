package com.giri.aiart.shared.persistence;

import com.giri.aiart.shared.domain.ArtworkEmbedding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/// JPA repository interface for {@link ArtworkEmbedding}
///
/// @author Giri Pottepalem
public interface ArtworkEmbeddingRepository extends JpaRepository<ArtworkEmbedding, UUID> {
}
