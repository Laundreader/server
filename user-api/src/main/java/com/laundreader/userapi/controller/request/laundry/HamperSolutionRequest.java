package com.laundreader.userapi.controller.request.laundry;

import com.laundreader.userapi.service.dto.HamperDTO;
import com.laundreader.userapi.service.dto.LaundrySymbolDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class HamperSolutionRequest {
    @NotNull(message = "값이 null 일 수 없습니다.")
    List<laundry> laundries;

    @Getter
    public static class laundry {
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
                        laundries.stream()
                                .map(l -> HamperDTO.LaundryDTO.builder()
                                        .id(l.getId())
                                        .materials(l.getMaterials())
                                        .color(l.getColor())
                                        .type(l.getType())
                                        .hasPrintOrTrims(l.getHasPrintOrTrims())
                                        .additionalInfo(l.getAdditionalInfo())
                                        .laundrySymbols(
                                                l.getLaundrySymbols() != null
                                                        ? l.getLaundrySymbols().stream()
                                                        .map(s -> LaundrySymbolDTO.builder()
                                                                .code(s.getCode())
                                                                .description(s.getDescription())
                                                                .build())
                                                        .toList()
                                                        : null
                                        ).solutions(
                                                l.getSolutions() != null
                                                        ? l.getSolutions().stream()
                                                        .map(s -> HamperDTO.SolutionDTO.builder()
                                                                .name(s.getName())
                                                                .contents(s.getContents())
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
