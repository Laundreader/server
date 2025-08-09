package com.laundreader.userapi.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class LaundrySymbolsDTO {
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private List<LaundrySymbolDetailDTO> waterWashing = new ArrayList<>();
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private List<LaundrySymbolDetailDTO> bleaching = new ArrayList<>();
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private List<LaundrySymbolDetailDTO> ironing = new ArrayList<>();
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private List<LaundrySymbolDetailDTO> dryCleaning = new ArrayList<>();
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private List<LaundrySymbolDetailDTO> wetCleaning = new ArrayList<>();
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private List<LaundrySymbolDetailDTO> wringing = new ArrayList<>();
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private List<LaundrySymbolDetailDTO> naturalDrying = new ArrayList<>();
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private List<LaundrySymbolDetailDTO> tumbleDrying = new ArrayList<>();

    @Getter
    public static class LaundrySymbolDetailDTO {
        private String code;
        private String description;
    }
}
