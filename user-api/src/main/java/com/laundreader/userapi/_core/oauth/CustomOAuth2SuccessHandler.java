package com.laundreader.userapi._core.oauth;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.laundreader.domain.entity.user.User;
import com.laundreader.domain.entity.user.UserOAuthToken;
import com.laundreader.domain.repository.user.UserOAuthTokenRepository;
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
	private final OAuth2AuthorizedClientService authorizedClientService;
	private final UserOAuthTokenRepository oAuthTokenRepository;

	@Override
	public void onAuthenticationSuccess(
		HttpServletRequest request,
		HttpServletResponse response,
		Authentication authentication
	) throws IOException {

		if (!(authentication instanceof OAuth2AuthenticationToken token)) {
			return;
		}

		// OAuth2 토큰 정보 조회
		OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
			token.getAuthorizedClientRegistrationId(),
			token.getName()
		);

		// Principal에서 User 가져오기
		User user = ((PrincipalDetails)authentication.getPrincipal()).getUser();

		// DB에 social token 갱신
		updateTokens(user, client);

		// 자체 Token 발급
		TokenResponse tokens = tokenService.generateTokens(user);

		// 쿠키 생성
		ResponseCookie accessTokenCookie = jwtCookieService.createAccessTokenCookie(tokens.getAccessToken());
		ResponseCookie refreshTokenCookie = jwtCookieService.createRefreshTokenCookie(tokens.getRefreshToken());

		// HttpServletResponse에 쿠키 추가
		response.setHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
		response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

		// 토큰 및 닉네임 전달을 위한 redirect
		String encodedNickname = URLEncoder.encode(user.getNickname(), StandardCharsets.UTF_8);
		response.sendRedirect(REDIRECT_URI + "?success=true&nickname=" + encodedNickname);
	}

	/**
	 * DB에 access token 갱신 및 refresh token 최초 저장
	 */
	private void updateTokens(User user, OAuth2AuthorizedClient client) {
		UserOAuthToken UserOAuthToken = oAuthTokenRepository.findByUserId(user.getId())
			.orElse(new UserOAuthToken(user));

		UserOAuthToken.setAccessToken(client.getAccessToken().getTokenValue());
		UserOAuthToken.setAccessTokenExpireAt(
			LocalDateTime.ofInstant(client.getAccessToken().getExpiresAt(), ZoneId.systemDefault())
		);
		if (UserOAuthToken.getRefreshToken() == null && client.getRefreshToken() != null) {
			UserOAuthToken.setRefreshToken(client.getRefreshToken().getTokenValue());
		}

		oAuthTokenRepository.save(UserOAuthToken);
	}
}
