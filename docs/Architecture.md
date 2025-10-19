# 🏗️ AI-Art System Architecture

## 1. Overview

The **AI-Art Platform** enables artists to upload, analyze, and generate **embeddings** for their artworks using **multimodal AI models**.  
It integrates **Spring AI**, **Ollama**, **MinIO**, and **PostgreSQL (pgvector)** to support semantic search, creative discovery, and multimodal intelligence workflows.

The platform’s goal is to create an intelligent art ecosystem — where each artwork is **stored, described, and vectorized** for downstream creative applications such as search, curation, and AI-assisted storytelling.

---

## 2. Core Modules

### 🧠 `aiart.modules.embeddings`
Responsible for generating **vector embeddings** for text and image data.  
It leverages Spring AI’s `EmbeddingModel` abstraction, which supports providers like **Ollama** and **OpenAI**.

- **Key Service:** `EmbeddingGeneratorService`
    - Generates deterministic embeddings using an AI model.
    - Generates random embeddings for tests or fallback cases.
    - Integrates with Spring AI’s `EmbeddingModel` for abstraction and portability.

---

### 🎨 `aiart.modules.ingestion`
Handles ingestion of new artworks via REST API and manages the full flow of storing and embedding art content.

- Uploads image files to **MinIO**.
- Invokes `EmbeddingGeneratorService` for embedding generation.
- Persists artwork metadata and embeddings into **PostgreSQL (pgvector)**.
- Exposes `/api/v1/ingest` REST endpoint.

**Primary Components:**
- `IngestionController` — REST interface for artwork ingestion.
- `IngestionService` — Core business service managing persistence and AI interactions.

---

### 📸 `aiart.media`
Provides **multimodal analysis** for visual inputs (images, videos, or future formats).  
Responsible for converting raw visual content into textual or structured semantic descriptions.

- **Core Interface:** `MediaService`
    - Methods like `analyzeMedia()` and `extractText()` use the AI model for visual interpretation.
    - Supports both streaming (`Flux<String>`) and synchronous calls.
- Uses **Spring AI’s ChatModel** or **Vision-Language Models** (e.g., Llava, Qwen, Bakllava, Gemini).

---

### 🧩 `aiart.shared`
Contains **common domain objects, enums, and utilities** shared across modules.

- **Domain Objects:** `Artwork`, `Artist`, `ArtworkEmbedding`
- **Enums:** `ArtType`, `EmbeddingType`, `EmbeddingStatusType`
- **Utilities:** `EmbeddingUtils`, `LogIcons`
- Encapsulates vector-related data structures and reusable constants.

---

## 3. Infrastructure

### 🗄️ Storage & Persistence
| Component | Purpose | Technology |
|------------|----------|------------|
| **PostgreSQL (pgvector)** | Stores artwork metadata and vector embeddings | `vector(1536)` column type |
| **MinIO** | Stores uploaded image files | S3-compatible object storage |
| **Flyway** | Database schema migration | Auto-runs during test setup |

---

### ⚙️ AI Runtime
Powered by **Spring AI**’s provider-agnostic architecture.

- Configurable **ChatModel** and **EmbeddingModel** through `application.yml`
- Supports multiple backends: **Ollama**, **OpenAI**, **Anthropic**, etc.
- Enables runtime model switching for multimodal workflows.

Example:
```yaml
spring:
  ai:
    model:
      chat: ollama
    ollama:
      chat:
        options:
          model: qwen2.5vl
          temperature: 0.2
```

## 4. Data Flow
[ Artist Uploads Image ]  
↓  
[ IngestionController ]  
↓  
[ MinIO Upload ] → [ File URL stored in Artwork entity ]  
↓  
[ MediaService.analyzeMedia() ] → AI-generated textual description  
↓  
[ EmbeddingGeneratorService ] → Vector embedding generation  
↓  
[ Artwork + Embeddings persisted in PostgreSQL (pgvector) ]  

### 🧜‍♀️🧜‍♂️ Sequence
sequenceDiagram
participant User
participant Controller
participant MinIO
participant MediaService
participant EmbeddingService
participant Postgres

```mermaid
    User->>Controller: POST /api/v1/ingest (image + metadata)
    Controller->>MinIO: upload image
    MinIO-->>Controller: file-url
    Controller->>MediaService: analyzeMedia(file-url)
    MediaService-->>Controller: description
    Controller->>EmbeddingService: generateEmbedding(description)
    EmbeddingService-->>Controller: vector
    Controller->>Postgres: save Artwork + Embedding
    Postgres-->>User: success
```

## 5. Testing Strategy
### ✅ Integration Tests (Testcontainers)
- Use pgvector/pgvector:pg18 Docker image for full-stack validation.
- Provide a real Postgres + pgvector environment for embeddings and persistence tests.
- Use @Testcontainers and @ServiceConnection for Spring-managed container wiring.

### 🧪 MockMVC & REST Layer Tests
- Mock Ollama and MinIO services for deterministic controller tests.
- Validate IngestionController REST flows and embedding persistence logic in isolation.
- Use MockMultipartFile for multipart uploads in controller tests.

## 6. Future Extensions
| Area| Description|
|-----|------------|
| Multimodal Prompt Tuning| Build structured prompt templates and tuning strategies to control model outputs (system / user / context separation).|
| Retrieval-Augmented Generation (RAG)	| Combine vector similarity search with LLMs for enriched responses and artist-aware generation.|
| Adaptive Captioning | Context-aware titles and narratives for artworks (artist-aware).|
| AI Collaboration Tools | Tools for artists to co-create or remix based on their own embeddings and metadata.|

## 7. Design Principles (TODO)
- Composable Modules: Modules are decoupled and can be replaced independently.
- Provider Agnostic AI Integration: Spring AI abstractions allow switching AI providers without major code changes.
- Observability: Use Spring Boot Actuator, Micrometer, and tracing (Zipkin) to monitor model calls and ingestion flows.
- Reproducible Tests: Testcontainers ensures integration tests run reliably across environments.
- Security & Privacy: Artist-owned artworks are stored in private MinIO buckets by default; access control can be enforced via pre-signed URLs and RBAC.

## 8. References
- [Spring AI Documentation]()
- [Ollama: Local model runtime]()
- [pgvector Extension]()
- [MinIO Documentation]()
- [Testcontainers]()

---
**Last Updated:** October 2025  
**Author:** Giri Pottepalem  
**Collaborator:** AI Architectural Assistant
Produce Mermaid