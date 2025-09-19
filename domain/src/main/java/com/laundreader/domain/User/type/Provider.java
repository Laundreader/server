package com.laundreader.domain.User.type;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Provider {
	NAVER,
	KAKAO;

	@JsonCreator
	public static Provider from(String value) {
		return Provider.valueOf(value.toUpperCase());
	}
}
