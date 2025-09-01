package com.laundreader.external.clova.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SingleSolutionDTO {
	@JsonSetter(nulls = Nulls.AS_EMPTY)
	private List<solution> solutions;

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class solution {
		private String name;
		private String contents;
	}
}
