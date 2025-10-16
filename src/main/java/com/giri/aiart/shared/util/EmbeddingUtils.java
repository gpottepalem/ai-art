package com.giri.aiart.shared.util;

import lombok.experimental.UtilityClass;

import java.util.Random;
import java.util.stream.IntStream;

/// Utility methods for Embeddings
///
/// @author Giri Pottepalem
@UtilityClass
public class EmbeddingUtils {
    private final Random random = new Random();

    /// Generates a 1536-dimensional float array with random values between -1.0 and 1.0,
    /// each rounded to six decimal places.
    ///
    /// @return A float array of size 1536 with random values.
    public float[] generateRandomEmbedding(int dimensions) {
        float[] embedding = new float[dimensions];
        IntStream.range(0, embedding.length)
            .forEach(i -> embedding[i] = Math.round((random.nextFloat() * 2 - 1) * 1_000_000.0) / 1_000_000.0f);
        return embedding;
    }
}
