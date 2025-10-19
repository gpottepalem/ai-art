package com.giri.aiart.modules.ingestion;

import com.giri.aiart.config.TestcontainersConfig;
import com.giri.aiart.shared.domain.Artist;
import com.giri.aiart.shared.domain.type.ArtType;
import com.giri.aiart.shared.domain.type.EmbeddingStatusType;
import com.giri.aiart.shared.domain.type.EmbeddingType;
import com.giri.aiart.shared.dto.ArtworkDTO;
import com.giri.aiart.shared.dto.ArtworkEmbeddingDTO;
import com.giri.aiart.shared.persistence.ArtistRepository;
import com.giri.aiart.shared.persistence.ArtworkEmbeddingRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaApi.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/// Integration test for {@link IngestionController}.
/// This runs a full end-to-end HTTP test using Testcontainers and TestRestTemplate.
/// Steps:
///  1️⃣ Persist Artist
///  2️⃣ POST multipart /api/v1/ingest
///  3️⃣ Verify JSON response
///
/// @author Giri Pottepalem
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfig.class)
public class IngestionControllerTestContainersIntegrationTest {

    @LocalServerPort private int port;

    @Autowired private TestRestTemplate restTemplate;
    @Autowired ArtistRepository  artistRepository;
    @Autowired private ArtworkEmbeddingRepository  artworkEmbeddingRepository;

    @MockitoBean private OllamaApi ollamaApi;

    private UUID artistId;

    /// Creates and persists a dummy artist before each test.
    @BeforeEach
    void setupAndSaveArtist() {
        artistId = artistRepository.saveAndFlush(
            Artist.builder().firstName("Giri").lastName("Pottepalem").bio("MockMvc Integration test artist").build()
        ).getId();
    }

    @BeforeEach
    void setupOllamaMock() {
        // Create a minimal Message
        Message mockMessage = new Message(
            Message.Role.USER,           // or Role.ASSISTANT depending on context
            "Fake description for test", // content
            List.of(),                   // images
            List.of(),                   // toolCalls
            null,                        // toolName
            null                         // thinking
        );

        // Create a mock ChatResponse
        OllamaApi.ChatResponse mockResponse = Mockito.mock(OllamaApi.ChatResponse.class);

        // Make the mock return a String when .message() is called
        Mockito.when(mockResponse.message()).thenReturn(mockMessage);

        // Make the ollamaApi.chat(...) call return the mock ChatResponse
        Mockito.when(ollamaApi.chat(Mockito.any()))
            .thenReturn(mockResponse);

        // Mock EmbeddingsResponse
        OllamaApi.EmbeddingsResponse mockEmbeddingsResponse = Mockito.mock(OllamaApi.EmbeddingsResponse.class);
        float[] fakeEmbedding = new float[1536]; // or some test vector
        Mockito.when(mockEmbeddingsResponse.embeddings())
            .thenReturn(List.of(fakeEmbedding));

        // Mock Ollama API for embeddings
        Mockito.when(ollamaApi.embed(Mockito.any()))
            .thenReturn(mockEmbeddingsResponse);
    }

    @Test
    void ingestArtwork_viaRestTemplate_shouldStoreArtworkAndEmbeddings() throws Exception {
        // 🖼️ Prepare mock image
        ByteArrayResource mockImage = new ByteArrayResource("fake-image-data".getBytes()) {
            @Override
            public String getFilename() {
                return "test.png";
            }
        };

        // and: body and headers for end-point POST
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("artistId", artistId.toString());
        body.add("title", "REST Template Artwork");
        body.add("description", "Artwork uploaded via TestRestTemplate");
        body.add("artType", ArtType.PAINTING.name());
        body.add("imageFile", mockImage);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // 🌐 Perform REST call
        ResponseEntity<ArtworkDTO> response = restTemplate.exchange(
            "http://localhost:%s/api/v1/ingest/%s".formatted(port, artistId),
            HttpMethod.POST,
            requestEntity,
            ArtworkDTO.class
        );

        // then: ✅ Verify response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        ArtworkDTO actualDto = response.getBody();
        assertThat(actualDto).isNotNull();
        // 🔬 Perform a deep comparison of the DTOs
        assertThat(actualDto)
            .usingRecursiveComparison()
            // Ignore the ID, embeddings, and minioKey fields, which are dynamic.
            .ignoringFields("id", "embeddings", "minioKey")
            .isEqualTo(new ArtworkDTO(
                null, // Expected ID is ignored, so we can pass null
                "REST Template Artwork",
                "Artwork uploaded via TestRestTemplate",
                ArtType.PAINTING,
                null, // Expected minioKey is ignored
                artistId,
                null // Expected embeddings list is ignored
            ));

        // 🔍 Verify the dynamic fields separately
        assertThat(actualDto.id()).isNotNull();
        assertThat(actualDto.minioKey()).startsWith("artworks/").endsWith("_test.png");
        assertThat(actualDto.embeddings()).hasSize(1);

        // 🔎 Verify properties of the nested embedding DTO
        ArtworkEmbeddingDTO embeddingDto = actualDto.embeddings().get(0);
        assertThat(embeddingDto.id()).isNotNull();
        assertThat(embeddingDto.type()).isEqualTo(EmbeddingType.IMAGE);
        assertThat(embeddingDto.status()).isEqualTo(EmbeddingStatusType.ACTIVE);
        assertThat(embeddingDto.embedding()).hasSize(1536);
        assertThat(embeddingDto.embedding()).containsOnly(0.0f);
    }
}
