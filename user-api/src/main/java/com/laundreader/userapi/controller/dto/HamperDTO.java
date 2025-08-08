package com.laundreader.userapi.controller.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class HamperDTO {
    List<laundryDTO> laundry;

    @Builder
    @Getter
    public static class laundryDTO{
        int id;
        List<String> materials;
        String color;
        String type;
        Boolean hasPrintOrTrims;
        List<String> additionalInfo;
        List<LaundrySymbol> laundrySymbols;
        List<Solution> solutions;

        @Builder
        @Getter
        public static class LaundrySymbol{
            String code;
            String description;
        }

        @Builder
        @Getter
        public static class Solution{
            String name;
            String contents;
        }
    }
}
