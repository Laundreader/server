package com.laundreader.userapi.controller.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class LaundryInfoDTO {
    List<String> materials;
    String color;
    String type;
    Boolean hasPrintOrTrims;
    List<String> additionalInfo;
    List<LaundrySymbol> laundrySymbols;


    @Builder
    @Getter
    public static class LaundrySymbol{
        String code;
        String description;
    }
}
