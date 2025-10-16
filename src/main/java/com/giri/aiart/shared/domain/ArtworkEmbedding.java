package com.giri.aiart.shared.domain;

import com.giri.aiart.shared.domain.type.EmbeddingStatusType;
import com.giri.aiart.shared.domain.type.EmbeddingType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;
import io.hypersistence.utils.hibernate.type.array.FloatArrayType;

import java.io.Serial;

/// Persistable entity
///
/// @author Giri Pottepalem
@Entity
@Table(name = "artwork_embeddings",
    indexes = {
        @Index(name = "idx_artwork_embeddings_artwork_id", columnList = "artwork_id")
    }
)

@Getter @Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class ArtworkEmbedding extends BaseAuditEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "artwork_id",  nullable = false)
    @ToString.Exclude // avoid recursion
    private Artwork artwork;

    @Column(name = "type", nullable = false, length = 32)
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @ToString.Include
    private EmbeddingType type = EmbeddingType.IMAGE; // default

    @Column(name = "status", nullable = false, length = 32)
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Builder.Default
    @ToString.Include
    private EmbeddingStatusType status = EmbeddingStatusType.ACTIVE; // default

    /// The embedding vector. Using PgVector extension, stored in a column "vector(N)".
    /// Hibernate 6 requires a custom JdbcType for VECTOR, typically via hibernate-vector module.
    @Column(name = "embedding", columnDefinition = "vector(1536)", nullable = false)
//    @JdbcTypeCode(value = SqlTypes.VECTOR)
    @Type(value = FloatArrayType.class)
    private float[] embedding;

/*
 TODO [Reverse Engineering] create field to map the 'embedding' column
 Available actions: Define target Java type | Uncomment as is | Remove column mapping
    @Column(name = "embedding", columnDefinition = "vector(1536)")
    private Object embedding;
*/
}