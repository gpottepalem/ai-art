package com.giri.aiart.domain;

import com.giri.aiart.domain.type.ArtMedia;

/// A record encapsulates painting properties
/// @author Giri Pottepalem
public record Painting(String artist, String name, ArtMedia media, String year) {
}
