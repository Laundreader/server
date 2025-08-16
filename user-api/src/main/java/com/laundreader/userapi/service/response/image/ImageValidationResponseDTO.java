package com.laundreader.userapi.service.response.image;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class ImageValidationResponseDTO {
    private final Image image;

    @Getter
    @AllArgsConstructor
    public static class Image {
        private final boolean isValid;
    }

    public ImageValidationResponseDTO(boolean isValid) {
        this.image = new Image(isValid);
    }
}
