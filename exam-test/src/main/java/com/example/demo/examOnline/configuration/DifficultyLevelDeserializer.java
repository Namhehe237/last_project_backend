package com.example.demo.examOnline.configuration;

import java.io.IOException;

import com.example.demo.examOnline.domain.enums.DifficultyLevel;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class DifficultyLevelDeserializer extends JsonDeserializer<DifficultyLevel> {
    
    @Override
    public DifficultyLevel deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        
        // Handle null, empty string, or whitespace-only strings
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        
        try {
            return DifficultyLevel.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            // If the value is not a valid enum, return null instead of throwing an exception
            return null;
        }
    }
}
