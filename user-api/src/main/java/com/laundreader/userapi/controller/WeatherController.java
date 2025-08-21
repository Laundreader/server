package com.laundreader.userapi.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.laundreader.common.util.ApiUtils;
import com.laundreader.userapi.service.WeatherService;
import com.laundreader.userapi.service.response.weather.CurrentWeatherResponseDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user-api/weather/")
@RequiredArgsConstructor
public class WeatherController {
	private final WeatherService weatherService;

	@GetMapping("/current")
	public ResponseEntity<ApiUtils.ApiResult<CurrentWeatherResponseDTO>> getCurrentWeather(
		@RequestParam double lat,
		@RequestParam double lon
	) {
		CurrentWeatherResponseDTO response = weatherService.getCurrentWeather(lat, lon);
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
