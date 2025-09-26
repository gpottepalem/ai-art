package com.giri.aiart.ollama;

import org.springframework.data.repository.ListCrudRepository;

/// A repository interface for {@link Art} domain object
/// @author Giri Pottepalem
public interface ArtRepository extends ListCrudRepository<Art, Integer> { }
