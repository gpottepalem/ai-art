package com.giri.aiart.shared.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

/// Base entity with auditable fields
///
/// @author Giri Pottepalem
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@SuperBuilder
@Getter @Setter
@ToString(onlyExplicitlyIncluded = true)
public abstract class BaseAuditEntity extends BaseEntity {
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP WITH TIMEZONE")
    @ToString.Include
    protected Instant createdAt;

    @LastModifiedDate
    @Column(name = "last_modified_at", updatable = false, columnDefinition = "TIMESTAMP WITH TIMEZONE")
    @ToString.Include
    protected Instant lastModifiedAt;
}
