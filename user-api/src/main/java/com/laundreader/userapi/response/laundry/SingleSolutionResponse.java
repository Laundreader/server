package com.laundreader.userapi.response.laundry;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SingleSolutionResponse {
	private List<SolutionDTO> solutions;

	@Getter
	@AllArgsConstructor
	public static class SolutionDTO {
		private String name;
		private String contents;
	}
}
