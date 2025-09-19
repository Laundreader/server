package com.laundreader.userapi._core.security.jwt.response;

import java.io.IOException;

import org.springframework.stereotype.Component;

import com.laundreader.common.error.exception.Exception400;
import com.laundreader.common.error.exception.Exception401;
import com.laundreader.common.error.exception.Exception403;
import com.laundreader.common.util.JsonConverter;

import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterResponse {
	private static JsonConverter jsonConverter;

	public FilterResponse(JsonConverter jsonConverter) {
		FilterResponse.jsonConverter = jsonConverter; // static 필드 초기화
	}

	public static void badRequest(HttpServletResponse resp, Exception400 e) throws IOException {
		resp.setStatus(e.status().value());
		resp.setContentType("application/json; charset=utf-8");
		String responseBody = jsonConverter.objectToJson(e.body());
		resp.getWriter().println(responseBody);
	}

	public static void unAuthorized(HttpServletResponse resp, Exception401 e) throws IOException {
		resp.setStatus(e.status().value());
		resp.setContentType("application/json; charset=utf-8");
		String responseBody = jsonConverter.objectToJson(e.body());
		resp.getWriter().println(responseBody);
	}

	public static void forbidden(HttpServletResponse resp, Exception403 e) throws IOException {
		resp.setStatus(e.status().value());
		resp.setContentType("application/json; charset=utf-8");
		String responseBody = jsonConverter.objectToJson(e.body());
		resp.getWriter().println(responseBody);
	}
}
