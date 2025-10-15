package com.offnal.shifterz.work.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Duration;

@Converter(autoApply = false)
public class DurationToStringConverter implements AttributeConverter<Duration, String> {
    @Override
    public String convertToDatabaseColumn(Duration duration) {
        // Duration -> String
        return duration != null ? duration.toString() : null;
    }

    @Override
    public Duration convertToEntityAttribute(String dbData) {
        // String -> Duration
        return dbData != null ? Duration.parse(dbData) : null;
    }
}
