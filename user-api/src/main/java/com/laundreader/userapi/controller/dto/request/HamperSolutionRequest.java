package com.laundreader.userapi.controller.dto.request;

import com.laundreader.userapi.controller.dto.HamperDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class HamperSolutionRequest {
    @NotNull(message = "값이 null 일 수 없습니다.")
    List<laundryDTO> laundry;

    @Getter
    public static class laundryDTO {
        @NotNull(message = "값이 null 일 수 없습니다.")
        private int id;
        private List<String> materials;
        private String color;
        private String type;
        private Boolean hasPrintOrTrims;
        private List<String> additionalInfo;
        private List<LaundrySymbol> laundrySymbols;
        private List<solution> solutions;

    }

    @Getter
    public static class LaundrySymbol{
        private String code;
        private String description;
    }

    @Getter
    public static class solution{
        String name;
        String contents;
    }

    public HamperDTO toHamperDTO() {
        return HamperDTO.builder()
                .laundry(
                        this.laundry.stream()
                                .map(l -> HamperDTO.laundryDTO.builder()
                                        .id(l.getId())
                                        .materials(l.getMaterials())
                                        .color(l.getColor())
                                        .type(l.getType())
                                        .hasPrintOrTrims(l.getHasPrintOrTrims())
                                        .additionalInfo(l.getAdditionalInfo())
                                        .laundrySymbols(
                                                l.getLaundrySymbols() != null
                                                        ? l.getLaundrySymbols().stream()
                                                        .map(s -> HamperDTO.laundryDTO.LaundrySymbol.builder()
                                                                .code(s.getCode())
                                                                .description(s.getDescription())
                                                                .build())
                                                        .toList()
                                                        : null
                                        )
                                        .build()
                                )
                                .toList()
                )
                .build();
    }


}
