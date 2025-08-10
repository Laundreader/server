package com.laundreader.userapi.controller.dto.request;

import com.laundreader.userapi.controller.dto.ImageDTO;
import com.laundreader.userapi.controller.dto.LaundryInfoDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class SingleSolutionRequest {
    private List<String> materials;
    private String color;
    private String type;
    private Boolean hasPrintOrTrims;
    private List<String> additionalInfo;
    private List<LaundrySymbol> laundrySymbols;
    private Image image;

    @Getter
    public static class LaundrySymbol{
        private String code;
        private String description;
    }

    @Getter
    public static class Image{
        @NotBlank(message = "빈 문자열 입니다.")
        private String format;
        @NotBlank(message = "빈 문자열 입니다.")
        private String data;
    }

    public LaundryInfoDTO toLaundryInfoDTO(){

        return LaundryInfoDTO.builder()
                .materials(materials)
                .color(color)
                .type(type)
                .hasPrintOrTrims(hasPrintOrTrims)
                .additionalInfo(additionalInfo)
                .laundrySymbols(
                    laundrySymbols == null ? null :
                            laundrySymbols.stream()
                                    .map(symbol -> LaundryInfoDTO.LaundrySymbol.builder()
                                            .code(symbol.code)
                                            .description(symbol.description)
                                            .build()
                                    )
                                    .collect(Collectors.toList())
                )
                .build();
    }

    public ImageDTO toImageDTO(){
        if (image == null) {
            return null;
        }
        return ImageDTO.builder()
                .format(image.format)
                .data(image.data)
                .build();
    }
}
