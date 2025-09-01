package com.laundreader.external.clova.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SummaryResultDTO {
	private String text;
	private int tokenCount;
}
