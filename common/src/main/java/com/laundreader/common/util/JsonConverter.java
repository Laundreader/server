package com.laundreader.common.util;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laundreader.common.error.exception.Exception500;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JsonConverter {
	private final ObjectMapper objectMapper;

	public String objectToJson(Object obj) {
		String jsonStr = "";
		try {
			jsonStr = objectMapper.writeValueAsString(obj);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception500("objectToJson 파싱 오류");
		}
		return jsonStr;
	}

	public <T> T jsonToObject(String json, Class<T> clazz) {
		T obj = null;
		try {
			obj = objectMapper.readValue(json, clazz);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception500("jsonToObject 파싱 오류");
		}
		return obj;
	}
}
