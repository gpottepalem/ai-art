package com.giri.aiart.shared.persistence;

import com.giri.aiart.shared.domain.Artist;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

/// JPA test for {@link ArtistRepository}
///
/// @author Giri Pottepalem
public class ArtistRepositoryIntegrationTest extends BaseDataJpaTest {
    @Autowired
    private ArtistRepository artistRepository;

    @Test
    void save_saves_entity() {
        Artist artist = Artist.builder()
            .firstName("Giri")
            .lastName("Pottepalem")
            .bio("Art is not my profession, it is a passion.")
            .build();
        Artist savedArtist = artistRepository.save(artist);
        Optional<Artist> foundArtist = artistRepository.findById(savedArtist.getId());
        assertThat(foundArtist.get()).isEqualTo(savedArtist);
    }

    @Test
    public void testFindAll() {
        var testArtistsSize = 5;
        var artists = IntStream.rangeClosed(1, testArtistsSize).mapToObj(index ->
            Artist.builder()
                .firstName("Giri-" + index)
                .lastName("Pottepalem")
                .bio("Art is not my profession, it is a passion.")
                .build()
        ).toList();

        artistRepository.saveAll(artists);
        assertThat(artistRepository.findAll()).hasSize(testArtistsSize);
    }
}
