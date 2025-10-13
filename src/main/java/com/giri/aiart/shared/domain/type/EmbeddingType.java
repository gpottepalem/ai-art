package com.giri.aiart.shared.domain.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/// An enumeration of Embedding Type
///
/// @author Giri Pottepalem
@Getter
@ToString
@AllArgsConstructor
public enum EmbeddingType {
    IMAGE("Image"),
    TEXT("Text"),
    HYBRID("Hybrid"),;

    private final String label;
}
