package com.giri.aiart.modules.artist.web;

import com.giri.aiart.shared.domain.Artist;
import com.giri.aiart.modules.artist.service.ArtistService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/// TODO
///
/// @author Giri Pottepalem
@RestController
@RequestMapping("/api/v1/artists")
@AllArgsConstructor
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
