package com.laundreader.userapi.dto.laundry;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LaundrySymbolDTO {
	String code;
	String description;
}
