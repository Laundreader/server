package com.laundreader.userapi.service.weather;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laundreader.common.error.ErrorMessage;
import com.laundreader.common.error.exception.Exception500;
import com.laundreader.external.clova.service.ClovaStudioService;
import com.laundreader.external.openWeather.client.OpenWeatherClient;
import com.laundreader.external.openWeather.response.ForecastResponse;
import com.laundreader.external.openWeather.response.ReverseGeoCodeResponse;
import com.laundreader.userapi.response.weather.CurrentWeatherResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherService {
	private final OpenWeatherClient openWeatherClient;
	private final ClovaStudioService clovaStudioService;
	private final ObjectMapper objectMapper;
	private final RedisTemplate<String, String> redisTemplate;

	public CurrentWeatherResponse getCurrentWeather(double lat, double lon) {
		com.laundreader.external.openWeather.response.CurrentWeatherResponse response = openWeatherClient.getCurrentWeather(
			lat, lon);
		return CurrentWeatherResponse.from(response);
	}

	public String getDrySolution(double lat, double lon) {
		LocalTime now = LocalTime.now();

		// 지역 코드
		ReverseGeoCodeResponse geoCode = openWeatherClient.getGeoCode(lat, lon);
		// 날짜 결정 (21시 이후면 내일, 아니면 오늘)
		LocalDate forecastDate = now.isAfter(LocalTime.of(21, 0)) ? LocalDate.now().plusDays(1) : LocalDate.now();

		// 캐싱 확인
		String key = "forecast:" + geoCode.getName() + ":" + forecastDate.toString();
		String cachedData = redisTemplate.opsForValue().get(key);
		if (cachedData != null) {
			// 캐싱된 데이터가 있으면 그대로 반환
			log.info("캐시 사용 {}", key);
			return cachedData;
		}

		// 캐싱 없으면 솔루션 받아옴
		int forecastCnt = calForecastCnt(now);
		ForecastResponse forecast = openWeatherClient.getForecast(lat, lon, forecastCnt);
		String solution;
		try {
			solution = clovaStudioService.weatherDrySolution(objectMapper.writeValueAsString(forecast));
		} catch (JsonProcessingException e) {
			throw new Exception500(ErrorMessage.INTERNAL_ERROR);
		}

		// redis 캐싱
		LocalDateTime midnight = LocalDateTime.of(forecastDate.plusDays(1), LocalTime.MIDNIGHT);
		Duration ttl = Duration.between(LocalDateTime.now(), midnight);
		redisTemplate.opsForValue().set(key, solution, ttl);

		return solution;
	}

	private int calForecastCnt(LocalTime now) {
		int[] forecastHours = {0, 3, 6, 9, 12, 15, 18, 21};
		long count = Arrays.stream(forecastHours)
			.filter(h -> h > now.getHour())
			.count();
		return (int)(count == 0 ? forecastHours.length : count);
	}
}
