-- artists
CREATE TABLE artists (
     id                 UUID PRIMARY KEY,
     version            BIGINT NOT NULL,
     created_at         TIMESTAMP WITH TIME ZONE NOT NULL,
     last_modified_at   TIMESTAMP WITH TIME ZONE,
     first_name         VARCHAR(255) NOT NULL,
     last_name          VARCHAR(255) NOT NULL,
     bio                TEXT,
     profile_image_url  VARCHAR(1024)
);

-- CREATE TABLE artworks (
--     id        uuid PRIMARY KEY,
--     artist_id uuid REFERENCES artists (id),
-- );

