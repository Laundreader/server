package com.laundreader.external.openWeather.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ForecastResponse {
	private List<ForecastItem> list;
	private City city;

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class ForecastItem {
		private long dt;
		private Main main;
		private List<Weather> weather;
		private Clouds clouds;
		private Wind wind;
		private int visibility;
		private double pop;
		private Rain rain;
		private Snow snow;
		private Sys sys;
		private String dtTxt;
	}

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Main {
		private double temp;
		private double feelsLike;
		private double tempMin;
		private double tempMax;
		private int pressure;
		private int seaLevel;
		private int grndLevel;
		private int humidity;
		private double tempKf;
	}

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
	public static class Clouds {
		private int all;
	}

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Wind {
		private double speed;
		private int deg;
		private double gust;
	}

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Rain {
		@JsonProperty("3h")
		private double threeHour;
	}

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Snow {
		@JsonProperty("3h")
		private double threeHour;
	}

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Sys {
		private String pod; // day/night

	}

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class City {
		private long id;
		private String name;
		private Coord coord;
		private String country;
		private int population;
		private int timezone;
		private long sunrise;
		private long sunset;
	}

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Coord {
		private double lat;
		private double lon;
	}
}
