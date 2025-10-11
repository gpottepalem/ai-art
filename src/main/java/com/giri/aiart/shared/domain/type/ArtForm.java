package com.giri.aiart.shared.domain.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.modulith.NamedInterface;

/// An enumeration of Art Forms
/// @author Giri Pottepalem
@NamedInterface
@Getter
@ToString
@AllArgsConstructor
public enum ArtForm {
    DRAWING("Drawing"),
    PAINTING("Painting"),
    DIGITAL("Digital");

    private final String label;
}
