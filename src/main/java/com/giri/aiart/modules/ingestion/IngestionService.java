package com.giri.aiart.modules.ingestion;

import com.giri.aiart.shared.domain.Artwork;
import com.giri.aiart.shared.domain.type.ArtType;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/// Service responsible for ingesting artworks, generating embeddings, and storing images.
/// Works with MinIO and PGVector.
/// Provides API for uploading images and persisting embeddings.
///
/// @author Giri Pottepalem
public interface IngestionService {
    /// Upload an image of a specific artist to MinIO,
    ///   generate embeddings with a multimodal
    ///   and save embeddings into DB
    ///
    /// @param artistId the artist UUID
    /// @param title artwork title
    /// @param description artwork description
    /// @param artType the type of art (DRAWING, PAINTING, DIGITAL)
    /// @param imageFile the image file uploaded
    /// @return persisted Artwork with embeddings
    Artwork ingestArtwork(UUID artistId,
                          String title,
                          String description,
                          ArtType artType,
                          MultipartFile imageFile) throws Exception;

}
