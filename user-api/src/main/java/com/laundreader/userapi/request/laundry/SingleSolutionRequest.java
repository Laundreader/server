package com.laundreader.userapi.request.laundry;

import java.util.List;
import java.util.stream.Collectors;

import com.laundreader.userapi.dto.laundry.LaundryDTO;
import com.laundreader.userapi.dto.laundry.LaundrySymbolDTO;

import lombok.Getter;

@Getter
public class SingleSolutionRequest {
	private Laundry laundry;

	public LaundryDTO toLaundryDTO() {
		return LaundryDTO.builder()
			.materials(laundry.materials)
			.color(laundry.color)
			.type(laundry.type)
			.hasPrintOrTrims(laundry.hasPrintOrTrims)
			.additionalInfo(laundry.additionalInfo)
			.laundrySymbols(
				laundry.laundrySymbols == null ? null :
					laundry.laundrySymbols.stream()
						.map(symbol -> LaundrySymbolDTO.builder()
							.code(symbol.code)
							.description(symbol.description)
							.build()
						)
						.collect(Collectors.toList())
			)
			.build();
	}

	@Getter
	public static class Laundry {
		private List<String> materials;
		private String color;
		private String type;
		private Boolean hasPrintOrTrims;
		private List<String> additionalInfo;
		private List<LaundrySymbol> laundrySymbols;
	}

	@Getter
	public static class LaundrySymbol {
		private String code;
		private String description;
	}
}
