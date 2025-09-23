package com.laundreader.userapi.response.laundry;

import java.util.List;

import com.laundreader.domain.dto.laundry.LaundrySymbolDTO;
import com.laundreader.domain.dto.laundry.SolutionDTO;
import com.laundreader.domain.entity.laundry.Laundry;
import com.laundreader.userapi.dto.laundry.LaundryImageDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LaundryGetResponse {
	Long id;
	List<String> materials;
	String color;
	String type;
	Boolean hasPrintOrTrims;
	List<String> additionalInfo;
	List<LaundrySymbolDTO> laundrySymbols;
	LaundryImageDTO image;
	List<SolutionDTO> solutions;

	public static LaundryGetResponseBuilder toBuilderWithoutImage(Laundry l) {
		return builder()
			.id(l.getId())
			.materials(l.getMaterials())
			.color(l.getColor())
			.type(l.getType())
			.hasPrintOrTrims(l.getHasPrintOrTrims())
			.additionalInfo(l.getAdditionalInfo())
			.laundrySymbols(l.getLaundrySymbols())
			.solutions(l.getSolutions());
	}
}
