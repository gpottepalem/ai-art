package com.giri.aiart.modules.artist;

import com.giri.aiart.shared.domain.Artist;
import com.giri.aiart.shared.persistence.ArtistRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/// Artist Service
///
/// @author Giri Pottepalem
@Service
@AllArgsConstructor
public class ArtistService {
    private final ArtistRepository repository;

    public Artist create(Artist artist) {
        return repository.save(artist);
    }

    public Artist get(UUID id) {
        return repository.findById(id)
            .orElseThrow(() -> new RuntimeException(
                String.format("Artist with id: %s not found{}", id))
            );
    }

    public List<Artist> list() {
        return repository.findAll();
    }

    public Artist update(UUID id, Artist updated) {
        Artist artist = get(id);
        artist.setFirstName(updated.getFirstName());
        artist.setLastName(updated.getLastName());
        artist.setBio(updated.getBio());
        artist.setProfileImageUrl(updated.getProfileImageUrl());
        return repository.save(artist);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
