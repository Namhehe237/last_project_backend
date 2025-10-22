package com.example.demo.examOnline.domain.enums;

import com.example.demo.examOnline.configuration.DifficultyLevelDeserializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = DifficultyLevelDeserializer.class)
public enum DifficultyLevel {
    EASY,
    MEDIUM,
    HARD;
    
    @JsonCreator
    public static DifficultyLevel fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return DifficultyLevel.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    @JsonValue
    public String toValue() {
        return this.name();
    }
}