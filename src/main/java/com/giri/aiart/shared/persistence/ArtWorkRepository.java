package com.giri.aiart.shared.persistence;

import com.giri.aiart.shared.domain.Artwork;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/// JPA repository interface for {@link com.giri.aiart.shared.domain.Artwork}
///
/// @author Giri Pottepalem
public interface ArtWorkRepository extends JpaRepository<Artwork, UUID> {
}
