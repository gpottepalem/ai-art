package com.giri.aiart.shared.domain.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/// An enumeration of Art Media
/// Holds {@link ArtType} that the media belongs to
///
/// @author Giri Pottepalem
@Getter
@ToString
@AllArgsConstructor
public enum ArtMedia {
    WATERCOLOR(ArtType.PAINTING, "Watercolor"),
    OIL(ArtType.PAINTING,"Oil"),
    ACRYLIC(ArtType.PAINTING,"Acrylic"),

    PENCIL(ArtType.DRAWING, "Pencil"),
    INK(ArtType.DRAWING, "Ink"),
    PEN(ArtType.DRAWING, "Pen"),
    CHALK(ArtType.DRAWING, "Chalk"),
    CRAYON(ArtType.DRAWING, "Crayon"),
    CHAR_COAL(ArtType.DRAWING, "Charcoal"),
    ILLUSTRATION(ArtType.DRAWING, "Illustration"),

    VECTOR(ArtType.DIGITAL, "Vector"),
    TWO_D_ANIMATION(ArtType.DIGITAL, "2D Animation"),
    THREE_D_ANIMATION(ArtType.DIGITAL, "3D Animation"),
    AI(ArtType.DIGITAL, "AI Art"),
    DIGITAL_PHOTOGRAPHY(ArtType.DIGITAL, "Digital Photography");

    private final ArtType artType;
    private final String label;
}
