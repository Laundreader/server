package com.laundreader.external.openWeather.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReverseGeoCodeResponse {
	private String name;
}
