package com.giri.aiart.modules.ingestion;

import com.giri.aiart.modules.embeddings.EmbeddingGeneratorService;
import com.giri.aiart.modules.storage.MinioStorageService;
import com.giri.aiart.shared.domain.Artist;
import com.giri.aiart.shared.domain.Artwork;
import com.giri.aiart.shared.domain.ArtworkEmbedding;
import com.giri.aiart.shared.domain.type.ArtType;
import com.giri.aiart.shared.domain.type.EmbeddingType;
import com.giri.aiart.shared.persistence.ArtWorkRepository;
import com.giri.aiart.shared.persistence.ArtistRepository;
import com.giri.aiart.shared.persistence.ArtworkEmbeddingRepository;
import com.giri.aiart.shared.util.LogIcons;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/// **Implementation** of the {@link IngestionService} responsible for orchestrating
/// the complete artwork ingestion workflow.
///
/// Combines several modules:
/// - **MinIO Storage** → securely uploads and stores artwork images
/// - **MediaService** → uses AI to describe visual content
/// - **EmbeddingGeneratorService** → creates vector embeddings from AI-generated text
/// - **JPA Repositories** → persists artists, artworks, and embeddings into PostgreSQL (with pgvector)
///
/// **Workflow Overview:**
/// 1. Retrieve the `Artist` entity for the given `artistId`
/// 2. Upload the artwork image to MinIO storage
/// 3. Use the `MediaService` to generate a textual description of the image
/// 4. Generate embeddings using Spring AI’s `EmbeddingModel` through `EmbeddingGeneratorService`
/// 5. Persist the `Artwork` and its embeddings in the relational and vector stores
///
/// All operations are executed within a transactional boundary to ensure consistency.
///
/// @author Giri Pottepalem
@Slf4j
@RequiredArgsConstructor
@Service
public class IngestionServiceImpl implements IngestionService {
    private final ArtistRepository artistRepository;
    private final ArtWorkRepository artWorkRepository;
    private final ArtworkEmbeddingRepository artworkEmbeddingRepository;
    private final MinioStorageService minioStorageService;
    private final EmbeddingGeneratorService  embeddingGeneratorService;

    /// {@inheritDoc}
    @Override
    @Transactional
    public Artwork ingestArtwork(@NonNull UUID artistId,
                                 @NonNull String title,
                                 @NonNull String description,
                                 @NonNull ArtType artType,
                                 @NonNull MultipartFile imageFile) throws Exception {
        log.info("{} Starting ingestion for artistId={}, title={}", LogIcons.STARTUP, artistId, title);
        // 1. Validate artist
        Artist artist = artistRepository.findById(artistId)
            .orElseThrow(() -> new IllegalArgumentException(String.format("Artist with id %s not found", artistId)));

        // 2. Upload image to MinIO
        log.info("{} Uploading image: {} to MinIO...", LogIcons.ART_WORK, imageFile);
        String minioKey = minioStorageService.uploadFile(imageFile, "artworks/");
        log.info("{} Uploaded image to MinIO: {}", LogIcons.ART_WORK, minioKey);

        // 3. Build Artwork entity
        Artwork artwork = Artwork.builder()
            .artist(artist)
            .title(title)
            .description(description)
            .artType(artType)
            .minioKey(minioKey)
            .build();

        // 4. Generate embeddings using AI
        ArtworkEmbedding artworkEmbedding = embeddingGeneratorService.generateEmbedding(imageFile.getResource(), EmbeddingType.IMAGE);
        log.info("{} Generated embeddings for artwork...", LogIcons.OLLAMA);

        // 5. Persist Artwork and embeddings
        artwork.addEmbeddings(List.of(artworkEmbedding));

        Artwork savedArtwork = artWorkRepository.save(artwork);
        log.info("{} Artwork (id={}, title={}) set with minioKey({}) and embeddings persisted...",
            LogIcons.SUCCESS, savedArtwork.getId(), savedArtwork.getTitle(), minioKey);

        return savedArtwork;
    }
}
