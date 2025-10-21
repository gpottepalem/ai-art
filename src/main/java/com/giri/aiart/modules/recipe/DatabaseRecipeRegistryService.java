package com.giri.aiart.modules.recipe;

import com.giri.aiart.shared.domain.PromptRecipe;
import com.giri.aiart.shared.domain.type.RecipeStatus;
import com.giri.aiart.shared.persistence.PromptRecipeRepository;
import com.giri.aiart.shared.util.LogIcons;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/// Implementation of `RecipeRegistry` backed by a database repository.
/// Provides access to stored prompt recipes and ensures retrieval of the latest active version
/// for a given recipe key.
///
/// @author Giri Pottepalem
@Slf4j
@Service
@RequiredArgsConstructor
public class DatabaseRecipeRegistryService implements RecipeRegistry {

    private final PromptRecipeRepository recipeRepository;

    @Override
    public Optional<PromptRecipe> getLatestActiveRecipe(String name) {
        log.debug("🔍 Looking up latest active recipe for name: {}", name);

        return recipeRepository.findFirstByNameAndStatusOrderByLastModifiedAtDesc(name, RecipeStatus.FINAL)
            .map(recipe -> {
                log.info("✅ Found active recipe [{}] version [{}]", recipe.getName(), recipe.getVersion());
                return recipe;
            });
    }

    @Override
    public List<PromptRecipe> listAllRecipes(String name) {
        return recipeRepository.findByNameOrderByLastModifiedAtDesc(name);
    }

    @Override
    public List<PromptRecipe> listActiveRecipes() {
        return recipeRepository.findByStatusOrderByLastModifiedAtDesc(RecipeStatus.FINAL);
    }

    @Override
    public PromptRecipe saveRecipe(PromptRecipe recipe) {
//        recipe.setLastModifiedAt(java.time.Instant.now());
//        if (recipe.getCreatedAt() == null) recipe.setCreatedAt(recipe.getLastModifiedAt());
        log.info("{} Saved recipe [{}] version [{}]", LogIcons.PERSISTENCE, recipe.getName(), recipe.getVersion());
        return recipeRepository.save(recipe);
    }

}
