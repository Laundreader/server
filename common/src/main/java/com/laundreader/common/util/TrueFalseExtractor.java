package com.laundreader.common.util;

public class TrueFalseExtractor {
    public static boolean extractFirstTrueOrFalse(String input) {
        int trueIndex = input.indexOf("true");
        int falseIndex = input.indexOf("false");

        if (trueIndex == -1 && falseIndex == -1) {
            return false;  // true, false 둘 다 없으면 false 반환
        } else if (trueIndex == -1) {
            return false;
        } else if (falseIndex == -1) {
            return true;
        } else {
            // 둘 다 있으면 먼저 나오는 것 반환
            return (trueIndex < falseIndex) ? true : false;
        }
    }
}
