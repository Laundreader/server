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

	/*
	 * localhost 개발을 위한 코드
	 * */
	public ResponseCookie createLocalAccessTokenCookie(String token) {
		return CookieUtil.createLocalCookie(AppConstants.ACCESS_TOKEN_NAME_PREFIX, token,
			AppConstants.ACCESS_TOKEN_EXP / 1000);
	}

	public ResponseCookie createLocalRefreshTokenCookie(String token) {
		return CookieUtil.createLocalCookie(AppConstants.REFRESH_TOKEN_NAME_PREFIX, token,
			AppConstants.REFRESH_TOKEN_EXP / 1000);
	}

	public ResponseCookie clearLocalAccessTokenCookie() {
		return CookieUtil.deleteLocalCookie(AppConstants.ACCESS_TOKEN_NAME_PREFIX);
	}

	public ResponseCookie clearLocalRefreshTokenCookie() {
		return CookieUtil.deleteLocalCookie(AppConstants.REFRESH_TOKEN_NAME_PREFIX);
	}

	/*
	 * localhost 개발을 위한 코드
	 * */
}
