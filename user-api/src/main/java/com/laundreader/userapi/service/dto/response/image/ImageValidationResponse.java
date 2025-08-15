package com.laundreader.userapi.service.dto.response.image;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@AllArgsConstructor
public class ImageValidationResponse {
    private final Image image;

    @Getter
    @AllArgsConstructor
    public static class Image {
        private final boolean isValid;
    }

    public ImageValidationResponse(boolean isValid) {
        this.image = new Image(isValid);
    }
}
