package com.giri.aiart.shared.persistence;

import com.giri.aiart.shared.domain.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/// JPA repository interface for {@link Artist} entity
///
/// @author Giri Pottepalem
public interface ArtistRepository extends JpaRepository<Artist, UUID> {
}
