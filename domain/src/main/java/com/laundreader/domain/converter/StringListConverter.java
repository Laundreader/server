package com.laundreader.domain.converter;

import java.util.List;

import com.laundreader.common.util.JsonConverter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;

@Converter
@RequiredArgsConstructor
public class StringListConverter implements AttributeConverter<List<String>, String> {

	private final JsonConverter jsonConverter;

	@Override
	public String convertToDatabaseColumn(List<String> attribute) {
		return attribute == null ? null : jsonConverter.objectToJson(attribute);
	}

	@Override
	public List<String> convertToEntityAttribute(String dbData) {
		return dbData == null ? null : jsonConverter.jsonToObject(dbData, List.class);
	}
}
