package com.laundreader.userapi.service.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.laundreader.userapi.service.dto.LaundrySymbolsDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class LabelAnalysisResponse {
    private List<String> materials;
    private String color;
    private String type;
    private Boolean hasPrintOrTrims;
    private List<String> additionalInfo;
    private LaundrySymbolsDTO laundrySymbols;
}
