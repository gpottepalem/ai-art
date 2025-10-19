package com.giri.aiart.modules.ingestion;

import com.giri.aiart.shared.domain.Artwork;
import com.giri.aiart.shared.domain.type.ArtType;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/// **Service** responsible for ingesting artworks, generating AI embeddings, and storing associated media.
///
/// This service orchestrates the ingestion workflow for new artworks submitted by artists.
/// It performs the following operations:
/// - Uploads the image to object storage (MinIO)
/// - Analyzes the image using a multimodal AI model (via `MediaService`)
/// - Generates vector embeddings (via `EmbeddingGeneratorService` and Spring AI’s `EmbeddingModel`)
/// - Persists artwork metadata and embeddings into PostgreSQL with pgvector support
///
/// The resulting `Artwork` entity is enriched with AI-generated insights and
/// ready for retrieval, similarity search, or downstream creative analysis.
///
/// **Typical use cases:**
/// - Uploading digital paintings, hand-drawn sketches, or AI-generated art
/// - Enabling semantic search and discovery within an artist’s portfolio
///
/// @author Giri Pottepalem
public interface IngestionService {
    /// Ingests a new artwork, uploading the image, analyzing it with AI, generating embeddings,
    /// and persisting enriched `Artwork` and embeddings into the database.
    ///
    /// **Process:**
    /// 1. Uploads the image to MinIO under the artist’s namespace
    /// 2. Invokes a multimodal AI model to analyze and describe the artwork
    /// 3. Generates a fixed-length embedding vector using Spring AI’s `EmbeddingModel`
    /// 4. Saves the artwork metadata and embeddings into PostgreSQL (pgvector)
    ///
    /// @param artistId the unique identifier of the artist who owns the artwork
    /// @param title the artwork title, as provided by the artist
    /// @param description an optional textual description of the artwork
    /// @param artType the type of artwork (`DRAWING`, `PAINTING`, `DIGITAL`, etc.)
    /// @param imageFile the uploaded image file representing the artwork
    /// @return a persisted `Artwork` instance containing its embeddings and metadata
    /// @throws Exception if an error occurs during upload, analysis, or persistence
    Artwork ingestArtwork(
        UUID artistId,
        String title,
        String description,
        ArtType artType,
        MultipartFile imageFile
    ) throws Exception;

}
