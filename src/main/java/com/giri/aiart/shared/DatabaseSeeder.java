package com.giri.aiart.shared;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.giri.aiart.shared.domain.Artist;
import com.giri.aiart.shared.domain.PromptRecipe;
import com.giri.aiart.shared.persistence.ArtistRepository;
import com.giri.aiart.shared.persistence.PromptRecipeRepository;
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
    private static final String ARTISTS_SEED_DATA_JSON_FILE = "seed/sample-artists.json";
    private static final String RECIPES_SEED_DATA_JSON_FILE = "seed/sample-recipes.json";

    private final ArtistRepository artistRepository;
    private final PromptRecipeRepository promptRecipeRepository;
    private final Flyway flyway;
    private final ObjectMapper objectMapper;

    /// Runs after application start-up, but waits until Flyway has completed.
    @PostConstruct
    public void init() {
        waitForFlywayMigrations();
        seedArtistDataIfEmpty();
        seedRecipeDataIfEmpty();
    }

    /// Waits until Flyway has completed all migrations.
    private void waitForFlywayMigrations() {
        log.info("{} Waiting for Flyway migrations to complete...", LogIcons.TIMER);
        int retries = 10;
        while (retries-- > 0) {
            try {
                flyway.info(); // If Flyway is ready, this won't throw
                log.info("{} Flyway is ready. Proceeding with seeding...", LogIcons.SUCCESS);
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
        log.error("{} Flyway did not become ready after waiting. Skipping seeding.", LogIcons.ERROR);
    }

    /// Checks if artists are empty and seeds data (artists) if needed.
    @Transactional
    public void seedArtistDataIfEmpty() {
        try {
            long artistCount = artistRepository.count();
            if (artistCount > 0) {
                log.info("{} Artists already exist in DB ({}). Skipping seeding.", LogIcons.ARTIST, artistCount);
                return;
            }

            log.info("{} No artists found — seeding data...", LogIcons.DATA);

            var artists = loadArtistsSeedDataFromJsonFile();
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
            log.info("{} Seeded {} artists and their artworks successfully.", LogIcons.SEED, artists.size());
        } catch (DataAccessException ex) {
            log.error("{} Database not ready or migrations not complete. Skipping seeding.", LogIcons.ERROR, ex);
        } catch (Exception ex) {
            log.error("{} Failed to seed data", LogIcons.ERROR, ex);
        }
    }

    /// Checks if recipes are empty and seeds data (artists) if needed.
    @Transactional
    public void seedRecipeDataIfEmpty() {
        try {
            long recipesCount = promptRecipeRepository.count();
            if (recipesCount > 0) {
                log.info("{} recipes already exist in DB ({}). Skipping seeding.", LogIcons.ARTIST, recipesCount);
                return;
            }

            log.info("{} No recipes found — seeding data...", LogIcons.DATA);

            var recipes = loadRecipesSeedDataFromJsonFile();
            promptRecipeRepository.saveAll(recipes);

            log.info("{} Seeded {} recipes and their artworks successfully.", LogIcons.SEED, recipes.size());
        } catch (DataAccessException ex) {
            log.error("{} Database not ready or migrations not complete. Skipping seeding.", LogIcons.ERROR, ex);
        } catch (Exception ex) {
            log.error("{} Failed to seed data", LogIcons.ERROR, ex);
        }
    }

     /// Loads artist data either from sample seed JSON or utility.
    private List<Artist> loadArtistsSeedDataFromJsonFile() {
        try (InputStream inputStream = new ClassPathResource(ARTISTS_SEED_DATA_JSON_FILE).getInputStream()) {
            log.info("{} Loading artists from JSON file: {} ...", LogIcons.FILE, ARTISTS_SEED_DATA_JSON_FILE);
//            return List.of(objectMapper.readValue(inputStream, Artist[].class));
            return objectMapper.readValue(inputStream, new TypeReference<>() {});
        } catch (Exception ex) {
            log.warn("{} Could not load {}, using programmatic fallback.", LogIcons.WARNING,  ARTISTS_SEED_DATA_JSON_FILE, ex);
            return ArtistsUtil.buildArtists(1);
        }
    }

    /// Loads recipes from sample seed JSON file
    private List<PromptRecipe> loadRecipesSeedDataFromJsonFile() {
        try (InputStream inputStream = new ClassPathResource(RECIPES_SEED_DATA_JSON_FILE).getInputStream()) {
            log.info("{} Loading recipes from JSON file: {} ...", LogIcons.FILE,  RECIPES_SEED_DATA_JSON_FILE);
            return objectMapper.readValue(inputStream, new TypeReference<>() {});
        } catch(Exception ex) {
            log.warn("{} Could not load {}, NO programmatic fallback.", LogIcons.WARNING,   RECIPES_SEED_DATA_JSON_FILE, ex);
            return List.of();
        }
    }

}
