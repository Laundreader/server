package com.laundreader.external.clova.type;

public enum ImageAnalysisType {
    LABEL("세탁 라벨"),
    CLOTH("의류 전체");

    private final String value;

    ImageAnalysisType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
