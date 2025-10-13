package com.giri.aiart.shared.domain.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.modulith.NamedInterface;

/// An enumeration of Art Type
/// @author Giri Pottepalem
//@NamedInterface
@Getter
@ToString
@AllArgsConstructor
public enum ArtType {
    DRAWING("Drawing"),
    PAINTING("Painting"),
    DIGITAL("Digital"),
    SCULPTURE("Sculpture"),
    PHOTOGRAPHY("Photography"),
    MIXED_MEDIA("Mixed Media");

    private final String label;
}
