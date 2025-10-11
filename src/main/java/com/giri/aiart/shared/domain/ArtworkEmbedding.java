package com.giri.aiart.shared.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/// Persistable entity
///
/// @author Giri Pottepalem
@Entity
@Table(name = "artwork_embeddings")

@Getter @Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class ArtworkEmbedding extends BaseAuditEntity {
    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artwork_id",  nullable = false)
    private Artwork artwork;

    /// Type of this embedding (e.g. "image", "text", "combined", etc.)
    @Column(name = "embedding_type", length = 100, nullable = false)
    private String type;

    /// The embedding vector. Using PgVector extension, stored in a column "vector(N)".
    /// Hibernate 6 requires a custom JdbcType for VECTOR, typically via hibernate-vector module.
    @Column(name = "embedding", columnDefinition = "vector(1536)")
    @JdbcTypeCode(value = SqlTypes.VECTOR)
    private float[] embedding;

/*
 TODO [Reverse Engineering] create field to map the 'embedding' column
 Available actions: Define target Java type | Uncomment as is | Remove column mapping
    @Column(name = "embedding", columnDefinition = "vector(1536)")
    private Object embedding;
*/
}