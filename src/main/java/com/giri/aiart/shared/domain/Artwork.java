package com.giri.aiart.shared.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;
import java.util.UUID;

/// Persistable entity
///
/// @author Giri Pottepalem
@Entity
@Table(name = "artworks")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(onlyExplicitlyIncluded = true)
public class Artwork extends BaseAuditEntity{
    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    @Column(name = "title", length = Integer.MAX_VALUE)
    @ToString.Include
    private String title;

    @Column(name = "description", length = Integer.MAX_VALUE)
    @ToString.Include
    private String description;

    @Column(name = "minio_key", length = Integer.MAX_VALUE)
    @ToString.Include
    private String minioKey;

    @Column(name = "thumbnail_key", length = Integer.MAX_VALUE)
    @ToString.Include
    private String thumbnailKey;

    @ColumnDefault("false")
    @Column(name = "public")
    @ToString.Include
    private Boolean publicField;

    @Column(name = "metadata")
    @JdbcTypeCode(SqlTypes.JSON)
    @ToString.Include
    private Map<String, Object> metadata;

    @ToString.Include
    protected UUID getArtistId() {
        return artist != null ? artist.getId() : null;
    }
}