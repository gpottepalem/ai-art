package com.giri.aiart.shared;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.giri.aiart.shared.domain.Artist;
import com.giri.aiart.shared.persistence.ArtistRepository;
import com.giri.aiart.shared.util.ArtistsUtil;
import com.giri.aiart.shared.util.EmbeddingUtils;
import com.giri.aiart.shared.util.LogIcons;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

/// Seeds initial data into the database if no artists exist.
///
/// @author Giri Pottepalem
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "aiart.data.seeder.enabled", havingValue = "true", matchIfMissing = true)
public class DatabaseSeeder {
    private static final String SEED_DATA_JSON_FILE = "seed/sample-artists.json";

    private final ArtistRepository artistRepository;
    private final Flyway flyway;
    private final ObjectMapper objectMapper;

    /// Runs after application start-up, but waits until Flyway has completed.
    @PostConstruct
    public void init() {
        waitForFlywayMigrations();
        seedDataIfEmpty();
    }

    /// Waits until Flyway has completed all migrations.
    private void waitForFlywayMigrations() {
        log.info("{} Waiting for Flyway migrations to complete...", LogIcons.TIMER);
        int retries = 10;
        while (retries-- > 0) {
            try {
                flyway.info(); // If Flyway is ready, this won't throw
                log.info("{} Flyway is ready. Proceeding with ingestion...", LogIcons.SUCCESS);
                return;
            } catch (Exception e) {
                log.warn("{} Flyway not ready yet ({}). Retrying in 3s...", LogIcons.WARNING, e.getMessage());
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        log.error("{} Flyway did not become ready after waiting. Skipping ingestion.", LogIcons.ERROR);
    }

    /// Checks if tables are empty and seeds data (artists) if needed.
    @Transactional
    public void seedDataIfEmpty() {
        try {
            long artistCount = artistRepository.count();
            if (artistCount > 0) {
                log.info("{} Artists already exist in DB ({}). Skipping ingestion.", LogIcons.ARTIST, artistCount);
                return;
            }

            log.info("{} No artists found — ingesting seed data...", LogIcons.DATA);

            var artists = loadSeedDataFromJsonFile();
            // set entities references
            artists.forEach(artist -> {
                artist.getArtworks().forEach(artwork -> {
                    // Replace 5 element float[] from JSON with 1536 random float elements array, DB column vector(1536) requires exactly 1536
                    artwork.getEmbeddings().forEach(embedding ->
                        embedding.setEmbedding(EmbeddingUtils.generateRandomEmbedding(1536))
                    );
                    artwork.addEmbeddings(artwork.getEmbeddings()); // add embeddings to artwork with proper back reference
                });
                artist.addArtWorks(artist.getArtworks()); // ass artworks to artist with proper back reference
            });
            artistRepository.saveAll(artists);

            log.info("{} Seed data created successfully!", LogIcons.SEED);
            log.info("{} Seeded {} artists and their artworks successfully.", LogIcons.AI, artists.size());
        } catch (DataAccessException ex) {
            log.error("{} Database not ready or migrations not complete. Skipping ingestion.", LogIcons.ERROR, ex);
        } catch (Exception ex) {
            log.error("{} Failed to ingest seed data", LogIcons.ERROR, ex);
        }
    }

     /// Loads artist data either from JSON or utility.
    private List<Artist> loadSeedDataFromJsonFile() {
        try (InputStream inputStream = new ClassPathResource(SEED_DATA_JSON_FILE).getInputStream()) {
            log.info("{} Loading artists from JSON file...", LogIcons.FILE);
//            return List.of(objectMapper.readValue(inputStream, Artist[].class));
            return objectMapper.readValue(inputStream, new TypeReference<>() {});
        } catch (Exception e) {
            log.warn("{} Could not load seed_artists.json, using programmatic fallback.", LogIcons.WARNING);
            return ArtistsUtil.buildArtists(1);
        }
    }

}
