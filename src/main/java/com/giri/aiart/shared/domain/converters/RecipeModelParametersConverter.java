package com.giri.aiart.shared.domain.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.giri.aiart.shared.domain.RecipeModelParameters;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

/// JPA AttributeConverter for JSON <-> Object mapping
///
/// @author Giri Pottepalem
@Slf4j
@Converter(autoApply = true)
public class RecipeModelParametersConverter implements AttributeConverter<RecipeModelParameters, String> {
    private static final ObjectMapper mapper = new ObjectMapper();
    @Override
    public String convertToDatabaseColumn(RecipeModelParameters recipeModelParameters) {
        if (recipeModelParameters == null) return null;
        try {
            return mapper.writeValueAsString(recipeModelParameters);
        } catch (JsonProcessingException e) {
            log.error("Error serializing RecipeModelParameters: {}", recipeModelParameters, e);
            throw new IllegalStateException("Could not serialize RecipeModelParameters", e);
        }
    }

    @Override
    public RecipeModelParameters convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) return null;
        try {
            return mapper.readValue(dbData, RecipeModelParameters.class);
        } catch (JsonProcessingException e) {
            log.error("Error deserializing RecipeModelParameters JSON: {}", dbData, e);
            throw new IllegalStateException("Could not deserialize RecipeModelParameters", e);
        }
    }
}
