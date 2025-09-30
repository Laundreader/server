package com.laundreader.userapi.dto.laundry;

import java.util.List;

import com.laundreader.domain.entity.laundry.Laundry;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class HamperDTO {
	List<LaundryDTO> laundry;

	public static HamperDTO fromEntities(List<Laundry> laundries) {
		List<LaundryDTO> dtos = laundries.stream()
			.map(LaundryDTO::fromEntity)
			.toList();

		return HamperDTO.builder()
			.laundry(dtos)
			.build();
	}
}
