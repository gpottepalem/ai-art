package com.giri.aiart.shared.domain.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/// An enumeration of Embedding Status Type
///
/// @author Giri Pottepalem
@Getter
@ToString
@AllArgsConstructor
public enum EmbeddingStatusType {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    NEEDS_UPDATE("Needs Update"),
    ARCHIVED("Archived");

    private final String label;
}
