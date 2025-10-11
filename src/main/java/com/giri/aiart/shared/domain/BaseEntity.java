package com.giri.aiart.shared.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.UUID;

/// Bas entity with common attributes: id, version (for optimistic locking)
///
/// @author Giri Pottepalem
@MappedSuperclass
@NoArgsConstructor
@SuperBuilder
@Getter @Setter
@ToString(onlyExplicitlyIncluded = true)
public abstract class BaseEntity implements Serializable {
    @Id
    @GeneratedValue
    @Column(nullable = false, updatable = false)
    @ToString.Include
    private UUID id;

    /// For optimistic locking
    @Version
    @ToString.Include
    private Long version;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        // Determine effective classes (unproxied) for comparison
        Class<?> thisClass = Hibernate.getClass(this);
        Class<?> otherClass = Hibernate.getClass(o);

        if (thisClass != otherClass) return false;

        BaseEntity other = (BaseEntity) o;
        return this.getId() != null && this.getId().equals(other.getId());
    }

    @Override
    public final int hashCode() {
        // Use class identity + id for stable hash, or just id’s hash if id is non-null
        return (getId() != null) ? getId().hashCode() : 0;
    }
}
