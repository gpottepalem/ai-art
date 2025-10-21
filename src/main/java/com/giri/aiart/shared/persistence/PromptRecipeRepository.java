package com.giri.aiart.shared.persistence;

import com.giri.aiart.shared.domain.PromptRecipe;
import com.giri.aiart.shared.domain.type.RecipeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for accessing and managing {@link PromptRecipe} entities.
 *
 * Provides convenient finder methods for retrieving prompt recipes
 * by their key, status, and version.
 *
 * Uses the {@link com.giri.aiart.shared.domain.type.RecipeStatus} enum instead of boolean flags
 * to ensure clear, mutually exclusive lifecycle states for each recipe.
 *
 * @author Giri Pottepalem
 */
public interface PromptRecipeRepository extends JpaRepository<PromptRecipe, UUID> {
    /// Finds the most recently updated active recipe for a given key.
    ///
    /// @param key the unique recipe key
    /// @return the most recently updated active recipe, if any
    Optional<PromptRecipe> findFirstByNameAndStatusOrderByLastModifiedAtDesc(String key, RecipeStatus status);

    /// Finds all recipes for a given key, ordered by most recently updated first.
    ///
    /// @param key the unique recipe key
    /// @return list of recipes for the given key, sorted descending by update time
    List<PromptRecipe> findByNameOrderByLastModifiedAtDesc(String key);

    /// Finds all recipes currently in the given status, ordered alphabetically by key.
    ///
    /// @param status the recipe status (e.g., ACTIVE)
    /// @return list of all active recipes, sorted by key
    List<PromptRecipe> findByStatusOrderByLastModifiedAtDesc(RecipeStatus status);
}
