package com.giri.aiart.shared.domain;

import com.giri.aiart.shared.domain.type.ArtMedia;

/// A record encapsulates painting properties
/// @author Giri Pottepalem
public record Painting(String artist, String name, ArtMedia media, String year) {
}
