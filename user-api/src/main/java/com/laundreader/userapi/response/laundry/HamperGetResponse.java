package com.laundreader.userapi.response.laundry;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HamperGetResponse {
	private List<HamperLaundryDTO> hamper;

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class HamperLaundryDTO {
		private Long id;
		private String thumbnail;
	}
}
