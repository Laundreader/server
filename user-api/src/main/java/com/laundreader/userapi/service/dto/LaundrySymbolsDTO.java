package com.laundreader.userapi.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class LaundrySymbolsDTO {
    private List<LaundrySymbolDetailDTO> waterWashing;
    private List<LaundrySymbolDetailDTO> bleaching;
    private List<LaundrySymbolDetailDTO> ironing;
    private List<LaundrySymbolDetailDTO> dryCleaning;
    private List<LaundrySymbolDetailDTO> wetCleaning;
    private List<LaundrySymbolDetailDTO> wringing;
    private List<LaundrySymbolDetailDTO> naturalDrying;
    private List<LaundrySymbolDetailDTO> tumbleDrying;

    @Getter
    public static class LaundrySymbolDetailDTO {
        private String code;
        private String description;
    }
}
