package com.laundreader.external.clova.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class HamperSolutionDTO {
	@JsonSetter(nulls = Nulls.AS_EMPTY)
	private List<group> groups;

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class group {
		private int id;
		@JsonSetter(nulls = Nulls.AS_EMPTY)
		private String name;
		@JsonSetter(nulls = Nulls.AS_EMPTY)
		private String solution;
		@JsonSetter(nulls = Nulls.AS_EMPTY)
		private List<Integer> laundryIds;
	}
}
