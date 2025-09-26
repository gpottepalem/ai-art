package com.giri.aiart.ollama;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
record Art(@Id int id, String name, String owner, String description) {}