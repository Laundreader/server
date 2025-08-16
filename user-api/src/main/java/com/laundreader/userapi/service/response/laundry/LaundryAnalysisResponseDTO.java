package com.laundreader.userapi.service.response.laundry;

import com.laundreader.userapi.service.dto.LaundrySymbolDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class LaundryAnalysisResponseDTO {
    private List<String> materials;
    private String color;
    private String type;
    private Boolean hasPrintOrTrims;
    private List<String> additionalInfo;
    private LaundrySymbolsDTO laundrySymbols;

    @Getter
    @Setter
    public static class LaundrySymbolsDTO {
        private List<LaundrySymbolDTO> waterWashing = new ArrayList<>();
        private List<LaundrySymbolDTO> bleaching = new ArrayList<>();
        private List<LaundrySymbolDTO> ironing = new ArrayList<>();
        private List<LaundrySymbolDTO> dryCleaning = new ArrayList<>();
        private List<LaundrySymbolDTO> wetCleaning = new ArrayList<>();
        private List<LaundrySymbolDTO> wringing = new ArrayList<>();
        private List<LaundrySymbolDTO> naturalDrying = new ArrayList<>();
        private List<LaundrySymbolDTO> tumbleDrying = new ArrayList<>();
    }
}
