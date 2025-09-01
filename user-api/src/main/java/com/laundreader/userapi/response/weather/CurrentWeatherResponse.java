package com.laundreader.userapi.response.weather;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CurrentWeatherResponse {
	private double temp;
	private int humidity;
	private WeatherDTO weather;

	public static CurrentWeatherResponse from(
		com.laundreader.external.openWeather.response.CurrentWeatherResponse response) {
		WeatherDTO weather = response.getWeather().stream()
			.findFirst()
			.map(WeatherDTO::from)
			.orElse(null);

		return new CurrentWeatherResponse(
			response.getMain().getTemp(),
			response.getMain().getHumidity(),
			weather
		);
	}

	@Getter
	@Builder
	public static class WeatherDTO {
		private int id;
		private String main;
		private String description;
		private String icon;

		private static WeatherDTO from(com.laundreader.external.openWeather.response.CurrentWeatherResponse.Weather w) {
			return new WeatherDTO(w.getId(), w.getMain(), w.getDescription(), w.getIcon());
		}
	}
}