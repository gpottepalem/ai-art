package com.giri.aiart.shared.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.modulith.NamedInterface;

import java.util.UUID;

/// Persistable Artist entity
///
/// @author Giri Pottepalem
@NamedInterface
@Entity
@Table(name = "artists")
@Getter @Setter
@NoArgsConstructor //(access = AccessLevel.PROTECTED)
@ToString(onlyExplicitlyIncluded = true)
@Builder
@AllArgsConstructor
public class Artist extends BaseAuditEntity {
    @Column(nullable = false)
    @ToString.Include
    private String firstName;

    @Column(nullable = false)
    @ToString.Include
    private String lastName;

    @Column(length = 2 * 1024)
    private String bio;

    private String profileImageUrl; // stored in MinIO
}
