package com.example.demo.examOnline.domain.enums;

import com.example.demo.examOnline.configuration.QuestionTypeDeserializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = QuestionTypeDeserializer.class)
public enum QuestionType {
    MULTIPLE_CHOICE,
    ESSAY;
    
    @JsonCreator
    public static QuestionType fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return QuestionType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    @JsonValue
    public String toValue() {
        return this.name();
    }
}
