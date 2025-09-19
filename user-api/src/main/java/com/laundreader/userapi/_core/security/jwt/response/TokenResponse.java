package com.laundreader.userapi._core.security.jwt.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResponse {
	private final String accessToken;
	private final String refreshToken;
}
