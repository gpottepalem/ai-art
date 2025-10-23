package com.giri.aiart.shared.domain;

/// Prompt Recipe model parameters saved for PromptRecipe in DB as JSON
///
/// @author Giri Pottepalem
public record RecipeModelParameters(
    String model,
    double temperature,
    int maxTokens,
    String responseFormat
) {}
