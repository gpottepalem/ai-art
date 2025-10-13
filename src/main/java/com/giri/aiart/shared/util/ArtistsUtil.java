package com.giri.aiart.shared.util;

import com.giri.aiart.shared.domain.Artist;
import com.giri.aiart.shared.domain.Artwork;
import com.giri.aiart.shared.domain.ArtworkEmbedding;
import com.giri.aiart.shared.domain.type.ArtType;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

/// Utility class provides utility methods for building Artists
///
/// @author Giri Pottepalem
@UtilityClass
public class ArtistsUtil {

    /// Builds and returns artists and all associated entities for the given number of artists needed
    /// @param nArtists the number of artists needed to be built
    /// @return list of Artists
    public List<Artist> buildArtists(int nArtists) {
        return IntStream.rangeClosed(1, nArtists)
            .mapToObj( artistIndex -> {
                var artist = Artist.builder()
                    .firstName("Bapu")
                    .lastName("Sattiraju")
                    .bio("The greatest Telugu Artist who took line-art to new levels.")
                    .profileImageUrl("")
                    .build();

                List<Artwork> artWorks = IntStream.rangeClosed(1, 2)
                    .mapToObj( artWorkInex -> {
                        var artWork = Artwork.builder()
                            .artist(artist) // set Artist
                            .title("Dreamscape")
                            .description("An abstract digital art piece representing dreams.")
                            .artType(ArtType.DIGITAL)
                            .minioKey("artworks/dreamscape.png")
                            .thumbnailKey("thumbnails/dreamscape_thumb.png")
                            .metadata(Map.of("resolution", "1024x1024"))
                            .build();

                        var artWorkEmbedding = ArtworkEmbedding.builder()
                            .artwork(artWork) // set ArtWork
                            .embedding(new float[]{0.33f, 0.64f, 0.27f})
                            .build();

                        return artWork;
                    }).toList();

                artist.addArtWorks(artWorks); // add ArtWorks

                return artist;
            }).toList();
    }

}
