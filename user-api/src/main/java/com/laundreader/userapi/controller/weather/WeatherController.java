package com.laundreader.userapi.controller.weather;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.laundreader.common.util.ApiUtils;
import com.laundreader.userapi.response.weather.CurrentWeatherResponse;
import com.laundreader.userapi.service.weather.WeatherService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user-api/weather/")
@RequiredArgsConstructor
public class WeatherController {
	private final WeatherService weatherService;

	@GetMapping("/current")
	public ResponseEntity<ApiUtils.ApiResult<CurrentWeatherResponse>> getCurrentWeather(
		@RequestParam double lat,
		@RequestParam double lon
	) {
		CurrentWeatherResponse response = weatherService.getCurrentWeather(lat, lon);
		return new ResponseEntity<>(ApiUtils.success(response), HttpStatus.OK);
	}

	@GetMapping("/solution/dry")
	public ResponseEntity<ApiUtils.ApiResult<Object>> getDrySolution(
		@RequestParam double lat,
		@RequestParam double lon
	) {
		String response = weatherService.getDrySolution(lat, lon);
		return new ResponseEntity<>(ApiUtils.success(Map.of("message", response)), HttpStatus.OK);
	}
}
