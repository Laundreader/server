package com.laundreader.userapi.dto.laundry;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class HamperDTO {
	List<LaundryDTO> laundry;
}
