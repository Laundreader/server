package com.laundreader.userapi.request.laundry;

import java.util.List;
import java.util.stream.Collectors;

import com.laundreader.domain.dto.laundry.LaundrySymbolDTO;
import com.laundreader.domain.dto.laundry.SolutionDTO;
import com.laundreader.userapi.dto.laundry.LaundryDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class LaundrySaveRequest {
	@NotEmpty(message = "materials 는 비어 있을 수 없습니다")
	private List<String> materials;

	@NotBlank(message = "color 는 공백이 아니어야 합니다")
	private String color;

	@NotBlank(message = "type 는 공백이 아니어야 합니다")
	private String type;

	@NotNull(message = "hasPrintOrTrims 는 null이 아니어야 합니다.")
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
