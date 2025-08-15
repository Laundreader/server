package com.laundreader.userapi.service.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum ImageType {
    LABEL("label","세탁 라벨"),
    CLOTHES("clothes", "의류");

    private final String code;
    private final String value;

    ImageType(String code, String value) {
        this.code = code;
        this.value = value;
    }

    @JsonCreator
    public static ImageType fromCode(String code) {
        for (ImageType type : values()) {
            if (type.getCode().equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid type code: " + code);
    }
}
