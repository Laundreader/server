package com.laundreader.domain.converter;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.laundreader.common.util.JsonConverter;
import com.laundreader.domain.dto.laundry.LaundrySymbolDTO;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;

@Converter
@RequiredArgsConstructor
public class LaundrySymbolConverter implements AttributeConverter<List<LaundrySymbolDTO>, String> {

	private final JsonConverter jsonConverter;

	@Override
	public String convertToDatabaseColumn(List<LaundrySymbolDTO> attribute) {
		return attribute == null ? null : jsonConverter.objectToJson(attribute);
	}

	@Override
	public List<LaundrySymbolDTO> convertToEntityAttribute(String dbData) {
		return jsonConverter.jsonToCollection(
			dbData,
			new TypeReference<List<LaundrySymbolDTO>>() {
			}
		);
	}
}

