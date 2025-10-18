package com.giri.aiart.shared.persistence;

import com.giri.aiart.shared.domain.Artist;
import com.giri.aiart.shared.domain.Artwork;
import com.giri.aiart.shared.domain.ArtworkEmbedding;
import com.giri.aiart.shared.domain.type.EmbeddingStatusType;
import com.giri.aiart.shared.domain.type.EmbeddingType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/// Data jpa test for {@link ArtworkEmbeddingRepository}
///
/// @author Giri Pottepalem
public class ArtworkEmbeddingRepositoryIntegrationTest extends BaseDataJpaTest{
    @Autowired private ArtistRepository artistRepository;
    @Autowired private ArtWorkRepository artworkRepository;
    @Autowired private ArtworkEmbeddingRepository embeddingRepository;

    @Test
    void test_ArtworkEmbeddingRepository_insertAndRetrieveEmbedding() {
        // given: artist
        Artist artist = artistRepository.save(
            Artist.builder().firstName("Giri").lastName("Pottepalem").build()
        );

        // and: artwork
        Artwork artwork = artworkRepository.save(
            Artwork.builder().title("Test Artwork").artist(artist).build()
        );

        // and: embedding vector for the artwork
        float[] vector = new float[1536];
        vector[0] = 0.123f;
        vector[1] = 0.456f;

        // and: embedding
        ArtworkEmbedding embedding = ArtworkEmbedding.builder().artwork(artwork).embedding(vector).build();

        // when: artwork embedding is saved and retrieved
        embeddingRepository.save(embedding);
        var retrieved = embeddingRepository.findAll().get(0);

        // then: verify that embeddings got saved
        assertThat(retrieved.getEmbedding()).isNotNull();
        assertThat(retrieved.getId()).isInstanceOf(UUID.class);
        assertThat(retrieved)
            .extracting(ArtworkEmbedding::getType, ArtworkEmbedding::getStatus)
            .containsExactly(EmbeddingType.IMAGE, EmbeddingStatusType.ACTIVE);

        // and verify: vector embeddings
        assertThat(retrieved.getEmbedding()).hasSize(1536);
        assertThat(retrieved.getEmbedding()[0]).isEqualTo(0.123f);
        assertThat(retrieved.getEmbedding()[1]).isEqualTo(0.456f);
        assertThat(retrieved.getEmbedding()[2]).isEqualTo(0.0f);
    }

}
