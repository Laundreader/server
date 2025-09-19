package com.laundreader.userapi._core.security.jwt.service;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import com.laundreader.common.util.CookieUtil;
import com.laundreader.userapi._core.AppConstants;

@Service
public class JwtCookieService {
	public ResponseCookie createAccessTokenCookie(String token) {
		return CookieUtil.createCookie(AppConstants.ACCESS_TOKEN_NAME_PREFIX, token,
			AppConstants.ACCESS_TOKEN_EXP / 1000);
	}

	public ResponseCookie createRefreshTokenCookie(String token) {
		return CookieUtil.createCookie(AppConstants.REFRESH_TOKEN_NAME_PREFIX, token,
			AppConstants.REFRESH_TOKEN_EXP / 1000);
	}

	public ResponseCookie clearAccessTokenCookie() {
		return CookieUtil.deleteCookie(AppConstants.ACCESS_TOKEN_NAME_PREFIX);
	}

	public ResponseCookie clearRefreshTokenCookie() {
		return CookieUtil.deleteCookie(AppConstants.REFRESH_TOKEN_NAME_PREFIX);
	}
}
