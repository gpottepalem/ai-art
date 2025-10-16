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

/// A concrete implementation of {@link IngestionService}
/// Ingests artworks, generates embeddings, and stores images.
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
        Artist artist = artistRepository.findById(artistId)
            .orElseThrow(() -> new IllegalArgumentException(String.format("Artist with id %s not found", artistId)));

        log.info("{} Uploading image: {} to MinIO...", LogIcons.ART_WORK, imageFile);
        String minioKey = minioStorageService.uploadFile(imageFile, "artworks/");
        log.info("{} Uploaded image to MinIO: {}", LogIcons.ART_WORK, minioKey);

        ArtworkEmbedding artworkEmbedding = embeddingGeneratorService.generateEmbedding(imageFile.getResource(), EmbeddingType.IMAGE);
        log.info("{} Generated embeddings for artwork...", LogIcons.OLLAMA);

        Artwork artwork = Artwork.builder()
            .artist(artist)
            .title(title)
            .description(description)
            .artType(artType)
            .minioKey(minioKey)
            .build();

        artwork.addEmbeddings(List.of(artworkEmbedding));

        Artwork savedArtwork = artWorkRepository.save(artwork);
        log.info("{} Artwork set with minioKey and embeddings persisted: {} (id={})", LogIcons.SUCCESS, savedArtwork.getTitle(), savedArtwork.getId());

        log.info("{} Successfully ingested ArtWork id: {}...", LogIcons.ART_WORK, savedArtwork.getId());
        return savedArtwork;
    }
}
