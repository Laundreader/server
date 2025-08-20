package com.laundreader.userapi.service.response.laundry;

import java.util.List;

import com.laundreader.userapi.service.dto.LaundrySymbolDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LaundryAnalysisResponseDTO {
	private List<String> materials;
	private String color;
	private String type;
	private Boolean hasPrintOrTrims;
	private List<String> additionalInfo;
	private List<LaundrySymbolDTO> laundrySymbols;
}
