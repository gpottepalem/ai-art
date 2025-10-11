package com.giri.aiart.shared.domain.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/// An enumeration of Art Style
/// @author Giri Pottepalem
@Getter
@ToString
@AllArgsConstructor
public enum ArtStyle {
    ABSTRACT("Abstract"),
    CUBISM("Cubism"),
    REALISM("Realism"),
    PHOTO_REALISM("Photo Realism"),
    IMPRESSIONISM("Impressionism"),
    EXPRESSIONISM("Expressionism");

    private final String label;
}
