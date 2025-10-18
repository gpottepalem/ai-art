package com.giri.aiart.shared.dto;

import com.giri.aiart.shared.domain.Artist;
import lombok.NonNull;

import java.util.UUID;

/// DTO for Artist entity — only exposes minimal public info
///
/// @author Giri Pottepalem
public record ArtistDTO(UUID id, String firstName, String lastName) {
    /// Create DTO from Domain object
    public static ArtistDTO from(@NonNull Artist artist) {
        return new ArtistDTO(
            artist.getId(), artist.getFirstName(), artist.getLastName()
        );
    }
}
