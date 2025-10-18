package com.giri.aiart.modules.ingestion;

import com.giri.aiart.shared.domain.Artwork;
import com.giri.aiart.shared.domain.type.ArtType;
import com.giri.aiart.shared.dto.ArtworkDTO;
import com.giri.aiart.shared.util.LogIcons;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/// REST API to ingest artwork images and generate embeddings
///
/// @author Giri Pottepalem
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/ingest")
public class IngestionController {
    private final IngestionService ingestionService;

    /// Uploads an image and triggers embeddings
    @PostMapping
    public ResponseEntity<ArtworkDTO> ingestArtWork(@RequestParam UUID artistId,
                                                    @RequestParam String title,
                                                    @RequestParam String description,
                                                    @RequestParam ArtType artType,
                                                    @RequestParam MultipartFile imageFile) throws Exception {
        log.info("{} Uploading Artwork...", LogIcons.DATA);
        Artwork artwork = ingestionService.ingestArtwork(artistId, title, description, artType, imageFile);
        var artworkDTO = ArtworkDTO.fromEntity(artwork);
        log.info("{} Artwork: {} uploaded and ingested...", LogIcons.DATA, artworkDTO);

        return ResponseEntity.ok(artworkDTO);
    }

}
