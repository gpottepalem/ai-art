-- artists
CREATE TABLE artists (
    id                  UUID PRIMARY KEY,
    version             BIGINT NOT NULL,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL,
    last_modified_at    TIMESTAMP WITH TIME ZONE,

    first_name          VARCHAR(255) NOT NULL,
    last_name           VARCHAR(255) NOT NULL,
    bio                 TEXT,
    profile_image_url   VARCHAR(1024)
);

CREATE TABLE artworks (
    id                  uuid PRIMARY KEY,
    version             bigint NOT NULL,
    created_at          timestamp WITH TIME ZONE NOT NULL, -- DEFAULT now(),
    last_modified_at    timestamp WITH TIME ZONE,

    artist_id           uuid NOT NULL,
    title               text,
    description         text,
    minio_key           text,          -- object store key
    thumbnail_key       text,
    public              boolean DEFAULT false,
    metadata            jsonb,

    CONSTRAINT fk_artwork_artist FOREIGN KEY(artist_id) REFERENCES artists(id)
);

-- pgvector column: store 1536-d floats for example (embedding dimensionality depends on model)
CREATE TABLE artwork_embeddings (
    id                  uuid PRIMARY KEY,
    version             bigint NOT NULL,
    created_at          timestamp WITH TIME ZONE NOT NULL, -- DEFAULT now(),
    last_modified_at    timestamp WITH TIME ZONE,

    artwork_id          uuid REFERENCES artworks(id),
    type                text,
    embedding           vector(1536)
);

CREATE INDEX ON artwork_embeddings USING ivfflat (embedding vector_cosine_ops) WITH (lists = 100);
