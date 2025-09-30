package com.laundreader.userapi.dto.laundry;

import java.util.List;

import com.laundreader.domain.dto.laundry.LaundrySymbolDTO;
import com.laundreader.domain.dto.laundry.SolutionDTO;
import com.laundreader.domain.entity.laundry.Laundry;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LaundryDTO {
	private final Long id;
	List<String> materials;
	String color;
	String type;
	Boolean hasPrintOrTrims;
	List<String> additionalInfo;
	List<LaundrySymbolDTO> laundrySymbols;
	List<SolutionDTO> solutions;

	// 엔티티 → DTO 변환
	public static LaundryDTO fromEntity(Laundry laundry) {
		return LaundryDTO.builder()
			.id(laundry.getId())
			.materials(laundry.getMaterials())
			.color(laundry.getColor())
			.type(laundry.getType())
			.hasPrintOrTrims(laundry.getHasPrintOrTrims())
			.additionalInfo(laundry.getAdditionalInfo())
			.laundrySymbols(laundry.getLaundrySymbols())
			.solutions(laundry.getSolutions())
			.build();
	}
}
