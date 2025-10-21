package com.giri.aiart.shared.domain;

import com.giri.aiart.shared.domain.type.RecipeStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/// Represents a versioned prompt recipe used for multimodal prompt execution.
/// Each recipe defines its model, parameters, and instructions for generating consistent prompts.
/// Recipes can evolve through tuning and carry an active flag to indicate the latest version.
///
/// @author Giri Pottepalem
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "prompt_recipe")
@ToString
public class PromptRecipe extends BaseAuditEntity {
    /// Logical recipe name (e.g. "artwork_description")
    @Column(nullable = false)
    private String name;

    /// Human-readable title, can be leveraged for manually following version convention
    /// Example: "V2 - Tuned for consistency "
    @Column(nullable = false)
    private String title;

    /// Whether this version is currently archived
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM) // for native PG enum type
    @Column(nullable = false)
    @Builder.Default
    private RecipeStatus status = RecipeStatus.DRAFT;

    /// JSON configuration text for model, temperature, etc.
    @Lob
    @Column(columnDefinition = "jsonb")
    private String parameters;

    /// Long recipe text — includes system instructions, markup, or structured response hints
    @Lob
    @Column(columnDefinition = "text")
    private String recipeTemplate;
}
