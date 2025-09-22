package com.laundreader.userapi._core.oauth;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.laundreader.domain.entity.user.User;
import com.laundreader.userapi._core.security.auth.PrincipalDetails;
import com.laundreader.userapi._core.security.jwt.response.TokenResponse;
import com.laundreader.userapi._core.security.jwt.service.JwtCookieService;
import com.laundreader.userapi._core.security.jwt.service.JwtTokenService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {
	private static final String REDIRECT_URI = "https://laundreader.com/auth/callback";
	private final JwtTokenService tokenService;
	private final JwtCookieService jwtCookieService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
		HttpServletResponse response,
		Authentication authentication) throws IOException {

		PrincipalDetails principal = (PrincipalDetails)authentication.getPrincipal();
		User user = principal.getUser();

		// Token 발급
		TokenResponse tokens = tokenService.generateTokens(user);

		// 쿠키 생성
		ResponseCookie accessTokenCookie = jwtCookieService.createAccessTokenCookie(tokens.getAccessToken());
		ResponseCookie refreshTokenCookie = jwtCookieService.createRefreshTokenCookie(tokens.getRefreshToken());

		// HttpServletResponse에 쿠키 추가
		response.setHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
		response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

		// 토큰 및 닉네임 전달을 위한 redirect
		String encodedNickName = URLEncoder.encode(user.getNickname(), StandardCharsets.UTF_8);
		response.sendRedirect(REDIRECT_URI + "?success=true&nickName=" + encodedNickName);
	}
}
