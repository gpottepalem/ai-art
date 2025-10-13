-- enums
CREATE TYPE art_type_enum AS ENUM ('DRAWING', 'PAINTING', 'DIGITAL', 'SCULPTURE', 'PHOTOGRAPHY', 'MIXED_MEDIA');
CREATE TYPE embedding_type_enum AS ENUM ('IMAGE', 'TEXT', 'HYBRID');
CREATE TYPE embedding_status_type_enum AS ENUM ('ACTIVE', 'INACTIVE', 'NEEDS_UPDATE', 'ARCHIVED');

-- artists
CREATE TABLE artists (
    id                  UUID PRIMARY KEY,
    version             BIGINT NOT NULL,
    created_at          timestamptz NOT NULL,
    last_modified_at    timestamptz,

    first_name          VARCHAR(255) NOT NULL,
    last_name           VARCHAR(255) NOT NULL,
    bio                 TEXT,
    profile_image_url   VARCHAR(1024)
);

CREATE TABLE artworks (
    id                  uuid PRIMARY KEY,
    version             bigint NOT NULL,
    created_at          timestamptz NOT NULL, -- DEFAULT now(),
    last_modified_at    timestamptz,

    artist_id           uuid NOT NULL,
    title               text,
    description         text,
    art_type            art_type_enum NOT NULL,
    minio_key           text,          -- object store key
    thumbnail_key       text,
    metadata            jsonb,

    CONSTRAINT fk_artwork_artist FOREIGN KEY(artist_id) REFERENCES artists(id)
);
-- Index on artist_id
CREATE INDEX idx_artworks_artist_id ON artworks (artist_id);


CREATE TABLE artwork_embeddings (
    id                  uuid PRIMARY KEY,
    version             bigint NOT NULL,
    created_at          timestamptz NOT NULL, -- DEFAULT now(),
    last_modified_at    timestamptz,

    artwork_id          uuid NOT NULL,
    type                embedding_type_enum NOT NULL,
    status              embedding_status_type_enum NOT NULL,
    embedding           vector(1536) NOT NULL, -- store 1536-d floats for example (embedding dimensionality depends on model)
    CONSTRAINT fk_artwork_embedding_artwork FOREIGN KEY (artwork_id)
        REFERENCES artworks(id)
);
-- Indexing: For similarity search
CREATE INDEX ON artwork_embeddings USING ivfflat (embedding vector_cosine_ops) WITH (lists = 100);
-- Index on artwork_id
CREATE INDEX idx_artwork_embeddings_artwork_id
    ON artwork_embeddings (artwork_id);
