package com.laundreader.domain.dto.laundry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LaundrySymbolDTO {
	String code;
	String description;
}
