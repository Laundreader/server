package com.laundreader.external.clova.service.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SummaryResult {
	private String text;
	private int tokenCount;
}
