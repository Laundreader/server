package com.laundreader.userapi.response.laundry;

import java.util.List;

import com.laundreader.userapi.dto.laundry.LaundrySymbolDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LaundryAnalysisResponse {
	private List<String> materials;
	private String color;
	private String type;
	private Boolean hasPrintOrTrims;
	private List<String> additionalInfo;
	private List<LaundrySymbolDTO> laundrySymbols;
}
