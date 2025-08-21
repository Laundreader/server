package com.laundreader.external.openWeather;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laundreader.common.error.ErrorMessage;
import com.laundreader.common.error.exception.Exception500;
import com.laundreader.external.openWeather.response.CurrentWeatherResponse;
import com.laundreader.external.openWeather.response.ForecastResponse;
import com.laundreader.external.openWeather.response.ReverseGeoCodeResponse;

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

	@Value("${openWeather.reverseGeoCodePath}")
	private String reverseGeoCodePath;    // Reverse geocoding 요청 Path

	@Value("${openWeather.forecastPath}")
	private String forecastPath;    // forecast 요청 Path

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

	public ReverseGeoCodeResponse getGeoCode(double lat, double lon) {
		try {
			String responseBody = webClientBuilder.baseUrl(baseUrl).build()
				.get()
				.uri(uriBuilder -> uriBuilder
					.path(reverseGeoCodePath)
					.queryParam("lat", lat)
					.queryParam("lon", lon)
					.queryParam("limit", 1)
					.queryParam("appid", defaultKey)
					.build())
				.retrieve()
				.bodyToMono(String.class)
				.block();

			List<ReverseGeoCodeResponse> results = Arrays.asList(
				objectMapper.readValue(responseBody, ReverseGeoCodeResponse[].class)
			);
			return results.getFirst();
		} catch (WebClientResponseException e) {
			log.error("OpenWeather 지역 코드 요청 실패: {}", e.getResponseBodyAsString());
			throw new Exception500(ErrorMessage.OPEN_WEATHER_REQUEST_FAILED + e.getResponseBodyAsString());
		} catch (Exception e) {
			log.error("OpenWeather 지역 코드 응답 파싱 실패", e);
			throw new Exception500(ErrorMessage.OPEN_WEATHER_RESPONSE_PARSING_FAILED);
		}
	}

	public ForecastResponse getForecast(double lat, double lon, int forecastCnt) {
		try {
			String responseBody = webClientBuilder.baseUrl(baseUrl).build()
				.get()
				.uri(uriBuilder -> uriBuilder
					.path(forecastPath)
					.queryParam("lat", lat)
					.queryParam("lon", lon)
					.queryParam("appid", defaultKey)
					.queryParam("cnt", forecastCnt)
					.queryParam("units", "metric") // 섭씨
					.queryParam("lang", "kr")      // 한국어
					.build())
				.retrieve()
				.bodyToMono(String.class)
				.block();
			return objectMapper.readValue(responseBody, ForecastResponse.class);
		} catch (WebClientResponseException e) {
			log.error("OpenWeather 예보 요청 실패: {}", e.getResponseBodyAsString());
			throw new Exception500(ErrorMessage.OPEN_WEATHER_REQUEST_FAILED + e.getResponseBodyAsString());
		} catch (Exception e) {
			log.error("OpenWeather 예보  응답 파싱 실패", e);
			throw new Exception500(ErrorMessage.OPEN_WEATHER_RESPONSE_PARSING_FAILED);
		}
	}
}
