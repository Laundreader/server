package com.laundreader.userapi.controller.request.laundry;

import com.laundreader.userapi.service.dto.LaundryDTO;
import com.laundreader.userapi.service.dto.LaundrySymbolDTO;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class SingleSolutionRequest {
    private Laundry laundry;

    @Getter
    public static class Laundry{
        private List<String> materials;
        private String color;
        private String type;
        private Boolean hasPrintOrTrims;
        private List<String> additionalInfo;
        private List<LaundrySymbol> laundrySymbols;
    }

    @Getter
    public static class LaundrySymbol{
        private String code;
        private String description;
    }

    public LaundryDTO toLaundryDTO(){
        return LaundryDTO.builder()
                .materials(laundry.materials)
                .color(laundry.color)
                .type(laundry.type)
                .hasPrintOrTrims(laundry.hasPrintOrTrims)
                .additionalInfo(laundry.additionalInfo)
                .laundrySymbols(
                        laundry.laundrySymbols == null ? null :
                                laundry.laundrySymbols.stream()
                                    .map(symbol -> LaundrySymbolDTO.builder()
                                            .code(symbol.code)
                                            .description(symbol.description)
                                            .build()
                                    )
                                    .collect(Collectors.toList())
                )
                .build();
    }
}
