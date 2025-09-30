package com.laundreader.userapi.request.laundry;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class HamperSolutionRequest {
	@NotNull(message = "값이 null 일 수 없습니다.")
	private List<Long> laundryIds;
}
