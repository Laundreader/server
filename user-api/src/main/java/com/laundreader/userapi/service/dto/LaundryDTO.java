package com.laundreader.userapi.service.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class LaundryDTO {
    List<String> materials;
    String color;
    String type;
    Boolean hasPrintOrTrims;
    List<String> additionalInfo;
    List<LaundrySymbolDTO> laundrySymbols;
}
