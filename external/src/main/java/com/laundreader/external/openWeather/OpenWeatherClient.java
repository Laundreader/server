package com.laundreader.external.openWeather;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laundreader.common.error.ErrorMessage;
import com.laundreader.common.error.exception.Exception500;
import com.laundreader.external.openWeather.response.CurrentWeatherResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OpenWeatherClient {
	private final WebClient.Builder webClientBuilder;
	private final ObjectMapper objectMapper;

	@Value("${openWeather.defaultKey}")
	private String defaultKey;    // Authorization Key

	@Value("${openWeather.baseUrl}")
	private String baseUrl;    // open weather baseUrl

	@Value("${openWeather.currentWeatherDataPath}")
	private String currentWeatherDataPath;    // currentWeatherData 요청 Path

	public CurrentWeatherResponse getCurrentWeather(double lat, double lon) {
		try {
			String responseBody = webClientBuilder.baseUrl(baseUrl).build()
				.get()
				.uri(uriBuilder -> uriBuilder
					.path(currentWeatherDataPath)
					.queryParam("lat", lat)
					.queryParam("lon", lon)
					.queryParam("appid", defaultKey)
					.queryParam("units", "metric") // 섭씨
					.queryParam("lang", "kr")      // 한국어
					.build())
				.retrieve()
				.bodyToMono(String.class)
				.block();
			return objectMapper.readValue(responseBody, CurrentWeatherResponse.class);
		} catch (WebClientResponseException e) {
			log.error("OpenWeather 요청 실패: {}", e.getResponseBodyAsString());
			throw new Exception500(ErrorMessage.OPEN_WEATHER_REQUEST_FAILED + e.getResponseBodyAsString());
		} catch (Exception e) {
			log.error("OpenWeather 응답 파싱 실패", e);
			throw new Exception500(ErrorMessage.OPEN_WEATHER_RESPONSE_PARSING_FAILED);
		}
	}
}
