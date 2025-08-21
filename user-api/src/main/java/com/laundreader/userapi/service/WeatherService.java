package com.laundreader.userapi.service;

import org.springframework.stereotype.Service;

import com.laundreader.external.openWeather.OpenWeatherClient;
import com.laundreader.external.openWeather.response.CurrentWeatherResponse;
import com.laundreader.userapi.service.response.weather.CurrentWeatherResponseDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WeatherService {
	private final OpenWeatherClient openWeatherClient;

	public CurrentWeatherResponseDTO getCurrentWeather(double lat, double lon) {
		CurrentWeatherResponse response = openWeatherClient.getCurrentWeather(lat, lon);
		return CurrentWeatherResponseDTO.from(response);
	}
}
