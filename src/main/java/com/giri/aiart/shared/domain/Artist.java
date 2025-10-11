package com.giri.aiart.shared.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

/// Persistable entity
///
/// @author Giri Pottepalem
@Entity
@Table(name = "artists")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(onlyExplicitlyIncluded = true)
public class Artist extends BaseAuditEntity {
    private static final long serialVersionUID = 1L;

    @Column(nullable = false)
    @ToString.Include
    private String firstName;

    @Column(nullable = false)
    @ToString.Include
    private String lastName;

    @Column(length = 2 * 1024)
    private String bio;

    private String profileImageUrl; // stored in MinIO

    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Artwork> artworks;
}
