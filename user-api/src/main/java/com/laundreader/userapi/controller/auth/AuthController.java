package com.laundreader.userapi.controller.auth;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.laundreader.common.util.ApiUtils;
import com.laundreader.userapi._core.AppConstants;
import com.laundreader.userapi._core.security.jwt.response.TokenResponse;
import com.laundreader.userapi._core.security.jwt.service.JwtCookieService;
import com.laundreader.userapi.service.auth.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/auth/")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AuthController {
	private final AuthService authService;
	private final JwtCookieService jwtCookieService;

	@PostMapping("/reissue")
	public ResponseEntity<Object> reissue(
		@CookieValue(AppConstants.ACCESS_TOKEN_NAME_PREFIX) String oldAccessToken,
		@CookieValue(AppConstants.REFRESH_TOKEN_NAME_PREFIX) String oldRefreshToken
	) {
		// 토큰 재발행
		TokenResponse reissuedTokens = authService.reissue(oldAccessToken, oldRefreshToken);

		// 쿠키 생성
		ResponseCookie accessTokenCookie = jwtCookieService.createAccessTokenCookie(reissuedTokens.getAccessToken());
		ResponseCookie refreshTokenCookie = jwtCookieService.createRefreshTokenCookie(reissuedTokens.getRefreshToken());

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
		headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
		return new ResponseEntity<>(ApiUtils.success(null), headers, HttpStatus.OK);
	}
}
