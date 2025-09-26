package com.giri.aiart.domain.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/// An enumeration of Art Media
/// Holds {@link ArtForm} that the media belongs to
/// @author Giri Pottepalem
@Getter
@ToString
@AllArgsConstructor
public enum ArtMedia {
    WATERCOLOR(ArtForm.PAINTING, "Watercolor"),
    OIL(ArtForm.PAINTING,"Oil"),
    ACRYLIC(ArtForm.PAINTING,"Acrylic"),

    PENCIL(ArtForm.DRAWING, "Pencil"),
    INK(ArtForm.DRAWING, "Ink"),
    PEN(ArtForm.DRAWING, "Pen"),
    CHALK(ArtForm.DRAWING, "Chalk"),
    CRAYON(ArtForm.DRAWING, "Crayon"),
    CHAR_COAL(ArtForm.DRAWING, "Charcoal"),
    ILLUSTRATION(ArtForm.DRAWING, "Illustration"),

    VECTOR(ArtForm.DIGITAL, "Vector"),
    TWO_D_ANIMATION(ArtForm.DIGITAL, "2D Animation"),
    THREE_D_ANIMATION(ArtForm.DIGITAL, "3D Animation"),
    AI(ArtForm.DIGITAL, "AI Art"),
    DIGITAL_PHOTOGRAPHY(ArtForm.DIGITAL, "Digital Photography");

    private final ArtForm artForm;
    private final String label;
}
