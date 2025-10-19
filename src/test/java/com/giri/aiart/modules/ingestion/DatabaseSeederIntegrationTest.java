package com.giri.aiart.modules.ingestion;

import com.giri.aiart.config.TestcontainersConfig;
import com.giri.aiart.modules.embeddings.EmbeddingGeneratorService;
import com.giri.aiart.modules.storage.MinioStorageService;
import com.giri.aiart.shared.domain.Artist;
import com.giri.aiart.shared.domain.Artwork;
import com.giri.aiart.shared.domain.ArtworkEmbedding;
import com.giri.aiart.shared.domain.type.ArtType;
import com.giri.aiart.shared.domain.type.EmbeddingType;
import com.giri.aiart.shared.persistence.ArtistRepository;
import com.giri.aiart.shared.persistence.ArtworkEmbeddingRepository;
import com.giri.aiart.shared.util.EmbeddingUtils;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

/// Integration test for {@link IngestionServiceImpl}
///
/// Verifies end-to-end ingestion flow:
/// 1. Upload image → mocked MinIO
/// 2. Generate embedding → mocked embedding generator
/// 3. Persist artist, artwork, and embedding.
///
/// @author Giri Pottepalem
@SpringBootTest
@Transactional
@Import(TestcontainersConfig.class)
public class DatabaseSeederIntegrationTest {
    @Autowired
    private IngestionService ingestionService;

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    ArtworkEmbeddingRepository artworkEmbeddingRepository;

    @MockitoBean
    MinioStorageService minioStorageService;

    @MockitoBean
    EmbeddingGeneratorService embeddingGeneratorService;

    private Artist testArtist;

    @BeforeEach
    public void setup() {
        testArtist = artistRepository.save(
            Artist.builder()
                .firstName("Giri")
                .lastName("Pottepalem")
                .bio("Testing artist for ingestion")
                .build()
        );
    }

    @Test
    void ingestArtwork_shouldPersistArtworkAndEmbeddings() throws Exception {
        // given: testArtist saved
        // and: mock multipart image file for ingestion
        MockMultipartFile mockImage = new MockMultipartFile(
            "file",
            "test.png",
            "image/png",
            "fake-image-content".getBytes()
        );

        // and: a mocked minioStorageService call to upload file
        given(minioStorageService.uploadFile(any(), any()))
            .willReturn("artwork/test.png");

        // and: a fake ArtworkEmbedding
        ArtworkEmbedding mockedArtWorkEmbedding = ArtworkEmbedding.builder()
            .embedding(EmbeddingUtils.generateRandomEmbedding(1536))
            .type(EmbeddingType.IMAGE)
            .build();

        // and: a mocked embeddingGeneratorService call
        given(embeddingGeneratorService.generateEmbedding(any(), eq(EmbeddingType.IMAGE)))
            .willReturn(mockedArtWorkEmbedding);

        // when: the service method under test is called
        Artwork savedArtwork;
        savedArtwork = ingestionService.ingestArtwork(
            testArtist.getId(),
            "Test Art",
            "A test art piece for integration test",
            ArtType.PAINTING,
            mockImage
        );

        // then: verify result
        assertThat(savedArtwork.getId()).isNotNull();
        assertThat(savedArtwork.getArtist().getId()).isEqualTo(testArtist.getId());
        assertThat(savedArtwork.getMinioKey()).isEqualTo("artwork/test.png");
        assertThat(savedArtwork.getEmbeddings()).hasSize(1);
        assertThat(savedArtwork.getEmbeddings().getFirst().getEmbedding()).hasSize(1536);
        assertThat(savedArtwork.getEmbeddings().getFirst().getArtwork().getId()).isEqualTo(savedArtwork.getId());
    }
}
