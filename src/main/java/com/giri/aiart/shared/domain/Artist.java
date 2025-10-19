package com.giri.aiart.shared.domain;

import jakarta.persistence.*;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/// Persistable entity
///
/// @author Giri Pottepalem
@Entity
@Table(name = "artists")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(onlyExplicitlyIncluded = true)
public class Artist extends BaseAuditEntity {
    @Column(nullable = false)
    @ToString.Include
    private String firstName;

    @Column(nullable = false)
    @ToString.Include
    private String lastName;

    @Column(length = 2 * 1024)
    private String bio;

    private String profileImageUrl; // stored in MinIO

    @Builder.Default
    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Artwork> artworks =  new ArrayList<>();

    /// Helper: For a given art works to be added, sets this artist and adds those to the artist.
    public void addArtWorks(@NotNull List<Artwork> artworksToAdd) {
        artworksToAdd.forEach(artwork -> artwork.setArtist(this));
        if (this.artworks == null) {
            this.artworks = new ArrayList<>();
        }
        this.artworks.addAll(artworksToAdd);
    }
}
