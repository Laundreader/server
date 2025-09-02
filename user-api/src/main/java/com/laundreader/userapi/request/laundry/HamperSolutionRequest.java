package com.laundreader.userapi.request.laundry;

import java.util.List;

import com.laundreader.userapi.dto.laundry.HamperDTO;
import com.laundreader.userapi.dto.laundry.LaundryDTO;
import com.laundreader.userapi.dto.laundry.LaundrySymbolDTO;
import com.laundreader.userapi.dto.laundry.SolutionDTO;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class HamperSolutionRequest {
	@NotNull(message = "값이 null 일 수 없습니다.")
	private List<laundry> laundries;

	public HamperDTO toHamperDTO() {
		return HamperDTO.builder()
			.laundry(
				laundries.stream()
					.map(l -> LaundryDTO.withIdBuilder(l.getId())
						.materials(l.getMaterials())
						.color(l.getColor())
						.type(l.getType())
						.hasPrintOrTrims(l.getHasPrintOrTrims())
						.additionalInfo(l.getAdditionalInfo())
						.laundrySymbols(l
							.getLaundrySymbols() != null
							? l.getLaundrySymbols()
							.stream()
							.map(s -> LaundrySymbolDTO
								.builder()
								.code(s.getCode())
								.description(s.getDescription())
								.build())
							.toList()
							: null)
						.solutions(l.getSolutions() != null
							? l.getSolutions()
							.stream()
							.map(s -> SolutionDTO
								.builder()
								.name(s.getName())
								.contents(s.getContents())
								.build())
							.toList()
							: null)
						.build())
					.toList())
			.build();
	}

	@Getter
	public static class laundry {
		@NotNull(message = "값이 null 일 수 없습니다.")
		private int id;
		private List<String> materials;
		private String color;
		private String type;
		private Boolean hasPrintOrTrims;
		private List<String> additionalInfo;
		private List<LaundrySymbol> laundrySymbols;
		private List<solution> solutions;

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
