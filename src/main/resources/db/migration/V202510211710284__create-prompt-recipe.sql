-- Creates the prompt_recipe table used to store multimodal prompt recipes,
-- Author: Giri Pottepalem
-- Created: 2025-10-21
-- 1. Create the RecipeStatus enum type
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'recipe_status') THEN
CREATE TYPE recipe_status AS ENUM ('DRAFT', 'FINAL', 'ARCHIVED');
END IF;
END$$;

-- 2. Create the table
CREATE TABLE IF NOT EXISTS prompt_recipe (
     id                  UUID PRIMARY KEY,
     version             BIGINT NOT NULL,
     created_at          timestamptz DEFAULT CURRENT_TIMESTAMP NOT NULL,
     last_modified_at    timestamptz DEFAULT CURRENT_TIMESTAMP NOT NULL,

     name VARCHAR(255) NOT NULL,
     title VARCHAR(255) NOT NULL,
     status recipe_status NOT NULL DEFAULT 'DRAFT',
     model_parameters JSONB,
     recipe_template TEXT
);

-- 3. Indexes for efficient lookup
CREATE INDEX IF NOT EXISTS idx_prompt_recipe_name ON prompt_recipe(name);
CREATE INDEX IF NOT EXISTS idx_prompt_recipe_status ON prompt_recipe(status);
CREATE INDEX IF NOT EXISTS idx_prompt_recipe_last_modified_at ON prompt_recipe(last_modified_at DESC)
