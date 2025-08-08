package com.laundreader.common.util;

public class Base64Extractor {
    public static String extractBase64PlainText(String base64Data) {
        // data:image/어떤_타입;base64, 까지 제거
        return base64Data.replaceFirst("^data:image/[^;]+;base64,", "");
    }
}
