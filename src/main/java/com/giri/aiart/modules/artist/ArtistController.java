package com.giri.aiart.modules.artist;

import com.giri.aiart.shared.domain.Artist;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/// REST API for {@link Artist}
///
/// @author Giri Pottepalem
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/artists")
public class ArtistController {
    private final ArtistService service;

    @PostMapping
    public Artist createArtist(Artist artist) {
        return service.create(artist);
    }

    @GetMapping("/{id}")
    public Artist get(@PathVariable UUID id) {
        return service.get(id);
    }

    @GetMapping
    public List<Artist> list() {
        return service.list();
    }

    @PutMapping("/{id}")
    public Artist update(@PathVariable UUID id, @RequestBody Artist artist) {
        return service.update(id, artist);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }

}
