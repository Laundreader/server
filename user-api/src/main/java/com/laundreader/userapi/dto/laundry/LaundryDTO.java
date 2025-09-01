package com.laundreader.userapi.dto.laundry;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LaundryDTO {
	Integer id; // 선택적 필드
	List<String> materials;
	String color;
	String type;
	Boolean hasPrintOrTrims;
	List<String> additionalInfo;
	List<LaundrySymbolDTO> laundrySymbols;
	List<SolutionDTO> solutions;

	public static LaundryDTOBuilder withIdBuilder(int id) {
		return builder().id(id);
	}

	public static LaundryDTOBuilder withoutIdBuilder() {
		return builder();
	}
}
