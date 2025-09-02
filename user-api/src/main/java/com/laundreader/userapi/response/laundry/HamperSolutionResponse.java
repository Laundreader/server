package com.laundreader.userapi.response.laundry;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HamperSolutionResponse {
	private List<groupDTO> groups;

	@Getter
	@AllArgsConstructor
	public static class groupDTO {
		private int id;
		private String name;
		private String solution;
		private List<Integer> laundryIds;
	}
}
