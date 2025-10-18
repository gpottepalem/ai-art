package com.giri.aiart.modules.ingestion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.giri.aiart.config.TestcontainersConfig;
import com.giri.aiart.modules.embeddings.EmbeddingGeneratorService;
import com.giri.aiart.modules.storage.MinioStorageService;
import com.giri.aiart.shared.domain.Artist;
import com.giri.aiart.shared.domain.ArtworkEmbedding;
import com.giri.aiart.shared.domain.type.ArtType;
import com.giri.aiart.shared.domain.type.EmbeddingType;
import com.giri.aiart.shared.persistence.ArtistRepository;
import com.giri.aiart.shared.persistence.ArtworkEmbeddingRepository;
import com.giri.aiart.shared.util.EmbeddingUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/// MockMvc Integration test for {@link IngestionController}.
/// ✅ Runs fast
/// ✅ No container startup
/// ✅ Validates controller mapping and payloads
///
/// @author Giri Pottepalem
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(TestcontainersConfig.class)
public class IngestionControllerMockMvcIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ArtistRepository artistRepository;
    @Autowired private IngestionService ingestionService;
    @Autowired private ArtworkEmbeddingRepository artworkEmbeddingRepository;

    @MockitoBean MinioStorageService  minioStorageService;
    @MockitoBean EmbeddingGeneratorService embeddingGeneratorService;

    private Artist artist;

    /// Creates and persists a dummy artist before each test.
    @BeforeEach
    void setup() {
        artist = artistRepository.save(
            Artist.builder()
                .firstName("Giri")
                .lastName("Pottepalem")
                .bio("MockMvc Integration test artist")
                .build()
        );
    }

    @Test
    void ingestArtwork_REST_endpoint_should_store_Artwork_and_Embeddings() throws Exception {
        // given: a mock image
        MockMultipartFile mockImage = new MockMultipartFile(
            "imageFile", "sample.png", "image/png", "fake-image-data".getBytes()
        );

        // and: mock dependencies
        given(minioStorageService.uploadFile(any(), any()))
            .willReturn("artwork/sample.png");
        given(embeddingGeneratorService.generateEmbedding(any(), eq(EmbeddingType.IMAGE)))
            .willReturn(ArtworkEmbedding.builder()
                .embedding(EmbeddingUtils.generateRandomEmbedding(1536))
                .type(EmbeddingType.IMAGE)
                .build());

        // when: performing multipart POST to controller
        mockMvc.perform(
            MockMvcRequestBuilders.multipart("/api/v1/ingest")
                .file(mockImage)
                .param("artistId", artist.getId().toString())
                .param("title", "Test Artwork")
                .param("description", "Test artwork from controller")
                .param("artType", ArtType.PAINTING.name())
                .contentType(MediaType.MULTIPART_FORM_DATA)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("Test Artwork"))
        .andExpect(jsonPath("$.description").value("Test artwork from controller"))
        .andExpect(jsonPath("$.minioKey").value("artwork/sample.png"))
        .andExpect(jsonPath("$.artistId").value(artist.getId().toString()));
    }
}
