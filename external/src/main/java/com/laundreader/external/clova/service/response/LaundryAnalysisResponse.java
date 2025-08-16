package com.laundreader.external.clova.service.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class LaundryAnalysisResponse {
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
    private LaundrySymbols laundrySymbols;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LaundrySymbols {
        @JsonSetter(nulls = Nulls.AS_EMPTY)
        private List<Symbol> waterWashing = new ArrayList<>();

        @JsonSetter(nulls = Nulls.AS_EMPTY)
        private List<Symbol> bleaching = new ArrayList<>();

        @JsonSetter(nulls = Nulls.AS_EMPTY)
        private List<Symbol> ironing = new ArrayList<>();

        @JsonSetter(nulls = Nulls.AS_EMPTY)
        private List<Symbol> dryCleaning = new ArrayList<>();

        @JsonSetter(nulls = Nulls.AS_EMPTY)
        private List<Symbol> wetCleaning = new ArrayList<>();

        @JsonSetter(nulls = Nulls.AS_EMPTY)
        private List<Symbol> wringing = new ArrayList<>();

        @JsonSetter(nulls = Nulls.AS_EMPTY)
        private List<Symbol> naturalDrying = new ArrayList<>();

        @JsonSetter(nulls = Nulls.AS_EMPTY)
        private List<Symbol> tumbleDrying = new ArrayList<>();
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Symbol {
        private String code;
        private String description;
    }
}
