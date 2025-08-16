package com.laundreader.userapi.service.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LaundrySymbolDTO {
    String code;
    String description;
}
