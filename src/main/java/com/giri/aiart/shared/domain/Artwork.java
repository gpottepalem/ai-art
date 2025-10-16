package com.giri.aiart.shared.domain;

import com.giri.aiart.shared.domain.type.ArtType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/// Persistable entity
///
/// @author Giri Pottepalem
@Entity
@Table(name = "artworks",
    indexes = {
        @Index(name = "idx_artworks_artist_id", columnList = "artist_id")
    }
)

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(onlyExplicitlyIncluded = true)
public class Artwork extends BaseAuditEntity{
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    @Column(name = "title", length = Integer.MAX_VALUE)
    @ToString.Include
    private String title;

    @Column(name = "description", length = Integer.MAX_VALUE)
    @ToString.Include
    private String description;

    @Column(name = "art_type", nullable = false, length = 32)
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM) // for native PG enum type
    @ToString.Include
    private ArtType artType;

    @Column(name = "minio_key", length = Integer.MAX_VALUE)
    @ToString.Include
    private String minioKey;

    @Column(name = "thumbnail_key", length = Integer.MAX_VALUE)
    @ToString.Include
    private String thumbnailKey;

    @Column(name = "metadata")
    @JdbcTypeCode(SqlTypes.JSON)
    @ToString.Include
    private Map<String, Object> metadata;

    @Builder.Default
    @OneToMany(mappedBy = "artwork", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArtworkEmbedding> embeddings = new ArrayList<>();

    @ToString.Include
    protected UUID getArtistId() {
        return artist != null ? artist.getId() : null;
    }

    /// Helper: For a given art work embeddings to be added, sets this art work and adds those to the art work.
    public void addEmbeddings(List<ArtworkEmbedding> embeddingsToAdd) {
        embeddingsToAdd.forEach(embedding -> embedding.setArtwork(this));
        if (embeddings == null) {
            this.embeddings = new ArrayList<>();
        }
        this.embeddings.addAll(embeddingsToAdd);
    }
}
