package com.laundreader.common.util;

import com.laundreader.common.error.ErrorMessage;
import com.laundreader.common.error.exception.Exception500;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
public class PromptUtils {
    public static String loadPrompt(String promptFilePath) {
        try (InputStream inputStream = PromptUtils.class.getClassLoader().getResourceAsStream(promptFilePath)) {
            if (inputStream == null){
                log.error("프롬프트 파일 없음");
                throw new IOException("프롬프트 파일 없음");
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new Exception500(ErrorMessage.INTERNAL_ERROR + ": 프롬프트 파일 읽기 실패");
        }
    }
}
