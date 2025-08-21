package com.laundreader.external.openWeather.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrentWeatherResponse {
	private List<Weather> weather;
	private Main main;

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Weather {
		private int id;
		private String main;
		private String description;
		private String icon;
	}

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Main {
		private double temp;
		private int humidity;
	}
}
