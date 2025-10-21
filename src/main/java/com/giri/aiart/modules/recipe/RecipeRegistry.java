package com.giri.aiart.modules.recipe;

import com.giri.aiart.shared.domain.PromptRecipe;

import java.util.List;
import java.util.Optional;

/// Interface for recipe registry
///
/// @author Giri Pottepalem
public interface RecipeRegistry {

    /// Retrieves the latest active recipe for a given key.
    ///
    /// This method queries the database for all active recipes matching the provided key,
    /// orders them by version in descending order, and returns the most recent one.
    /// If no active recipe exists, it returns an empty `Optional`.
    ///
    /// @param key the unique key identifying the recipe (e.g., "describe_artwork")
    /// @return the latest active `PromptRecipe`, or an empty `Optional` if none found
    Optional<PromptRecipe> getLatestActiveRecipe(String key);

    /// Lists all versions for a recipe key (for admin or tuning purposes).
    List<PromptRecipe> listAllRecipes(String key);

    /// Lists all active recipes across all keys.
    List<PromptRecipe> listActiveRecipes();

    /// Persists a new or updated recipe version.
    PromptRecipe saveRecipe(PromptRecipe recipe);
}
