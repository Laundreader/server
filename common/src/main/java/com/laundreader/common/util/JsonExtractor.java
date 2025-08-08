package com.laundreader.common.util;

import java.util.ArrayDeque;
import java.util.Deque;

public class JsonExtractor {

    /**
     * 텍스트에서 가장 먼저 나오는 유효한 JSON 블록 ({...} 또는 [...])을 추출합니다.
     * 문자열 안에 포함된 중괄호는 무시하고 중첩 구조도 올바르게 처리합니다.
     *
     * @param text 원본 텍스트
     * @return 추출된 JSON 문자열 또는 null
     */
    public static String extractFirstJsonBlock(String text) {
        if (text == null || text.isBlank()) return null;

        int start = -1;
        char openingChar = 0;
        Deque<Character> stack = new ArrayDeque<>();
        boolean insideString = false;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            // 문자열 내 여부 판단 (escape 고려)
            if (c == '"') {
                boolean escaped = false;
                int j = i - 1;
                while (j >= 0 && text.charAt(j) == '\\') {
                    escaped = !escaped;
                    j--;
                }
                if (!escaped) {
                    insideString = !insideString;
                }
            }

            if (insideString) continue;

            // JSON 시작
            if (stack.isEmpty() && (c == '{' || c == '[')) {
                start = i;
                openingChar = c;
                stack.push(c);
                continue;
            }

            // 중첩 구조
            if (!stack.isEmpty()) {
                if ((c == '{' && openingChar == '{') || (c == '[' && openingChar == '[')) {
                    stack.push(c);
                } else if ((c == '}' && openingChar == '{') || (c == ']' && openingChar == '[')) {
                    stack.pop();
                    if (stack.isEmpty()) {
                        return text.substring(start, i + 1).trim();
                    }
                }
            }
        }

        return null;
    }
}
