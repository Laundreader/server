package com.laundreader.userapi.request.laundry;

import java.util.List;
import java.util.stream.Collectors;

import com.laundreader.domain.dto.laundry.LaundrySymbolDTO;
import com.laundreader.domain.dto.laundry.SolutionDTO;
import com.laundreader.userapi.dto.laundry.LaundryDTO;

import lombok.Getter;

@Getter
public class LaundrySaveRequest {
	private List<String> materials;
	private String color;
	private String type;
	private Boolean hasPrintOrTrims;
	private List<String> additionalInfo;
	private List<LaundrySymbol> laundrySymbols;
	private List<solution> solutions;

	public LaundryDTO toLaundryDTO() {
		return LaundryDTO.builder()
			.materials(materials)
			.color(color)
			.type(type)
			.hasPrintOrTrims(hasPrintOrTrims)
			.additionalInfo(additionalInfo)
			.laundrySymbols(
				laundrySymbols == null ? null :
					laundrySymbols.stream()
						.map(symbol -> LaundrySymbolDTO.builder()
							.code(symbol.code)
							.description(symbol.description)
							.build()
						)
						.collect(Collectors.toList())
			).solutions(solutions == null ? null :
				solutions.stream()
					.map(solution -> SolutionDTO.builder()
						.name(solution.getName())
						.contents(solution.getContents())
						.build())
					.toList()
			)
			.build();
	}

	@Getter
	public static class LaundrySymbol {
		private String code;
		private String description;
	}

	@Getter
	public static class solution {
		private String name;
		private String contents;
	}
}
