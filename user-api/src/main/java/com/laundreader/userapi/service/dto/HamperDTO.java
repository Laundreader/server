package com.laundreader.userapi.service.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class HamperDTO {
    List<LaundryDTO> laundry;

    @Builder
    @Getter
    public static class LaundryDTO {
        int id;
        List<String> materials;
        String color;
        String type;
        Boolean hasPrintOrTrims;
        List<String> additionalInfo;
        List<LaundrySymbolDTO> laundrySymbols;
        List<SolutionDTO> solutions;
    }

    @Builder
    @Getter
    public static class SolutionDTO {
        String name;
        String contents;
    }
}
