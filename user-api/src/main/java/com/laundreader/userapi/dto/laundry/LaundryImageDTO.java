package com.laundreader.userapi.dto.laundry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LaundryImageDTO {
	String label;
	String clothes;
}
