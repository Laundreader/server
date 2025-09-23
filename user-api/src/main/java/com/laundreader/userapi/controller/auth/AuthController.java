package com.laundreader.userapi.controller.auth;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
		@CookieValue(value = AppConstants.ACCESS_TOKEN_NAME_PREFIX, required = false) String oldAccessToken,
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

	@PreAuthorize("isAuthenticated()") // 인증 검사 SecurityConfig에서 관리되지만 메서드 단위에서 추가 명시
	@PostMapping("/logout")
	public ResponseEntity<Void> logout(
		@CookieValue(AppConstants.ACCESS_TOKEN_NAME_PREFIX) String accessToken,
		@CookieValue(AppConstants.REFRESH_TOKEN_NAME_PREFIX) String refreshToken
	) {
		// 서버단 토큰 무효화
		authService.logout(accessToken, refreshToken);

		// Max-Age=0으로 내려서 토큰 쿠키 무효화
		ResponseCookie accessTokenCookie = jwtCookieService.clearAccessTokenCookie();
		ResponseCookie refreshTokenCookie = jwtCookieService.clearRefreshTokenCookie();

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
		headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
		return new ResponseEntity<>(headers, HttpStatus.NO_CONTENT);
	}
}
