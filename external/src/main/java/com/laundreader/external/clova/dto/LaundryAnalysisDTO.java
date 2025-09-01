package com.laundreader.external.clova.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class LaundryAnalysisDTO {
	@JsonSetter(nulls = Nulls.AS_EMPTY)
	private List<String> materials;

	@JsonSetter(nulls = Nulls.AS_EMPTY)
	private String color;

	@JsonSetter(nulls = Nulls.AS_EMPTY)
	private String type;

	private Boolean hasPrintOrTrims;

	@JsonSetter(nulls = Nulls.AS_EMPTY)
	private List<String> additionalInfo;

	@JsonSetter(nulls = Nulls.AS_EMPTY)
	private List<Symbol> laundrySymbols;

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Symbol {
		private String code;
		private String description;
	}
}
