package com.laundreader.userapi.service.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.laundreader.userapi.service.dto.LaundrySymbolsDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class LabelAnalysisResponse {
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private List<String> materials;
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private String color;
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private String type;
    private Boolean hasPrintOrTrims;
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private List<String> additionalInfo;
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private LaundrySymbolsDTO laundrySymbols;
}
