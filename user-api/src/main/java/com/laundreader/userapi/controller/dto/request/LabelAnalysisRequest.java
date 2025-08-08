package com.laundreader.userapi.controller.dto.request;

import com.laundreader.userapi.controller.dto.ImageDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class LabelAnalysisRequest {
    @NotNull(message = "값이 null 일 수 없습니다.")
    private Image image;

    @Getter
    public static class Image{
        @NotBlank(message = "빈 문자열 입니다.")
        private String format;
        @NotBlank(message = "빈 문자열 입니다.")
        private String data;
    }

    public ImageDTO toImageDTO(){
        return ImageDTO.builder()
                .format(image.format)
                .data(image.data)
                .build();
    }
}