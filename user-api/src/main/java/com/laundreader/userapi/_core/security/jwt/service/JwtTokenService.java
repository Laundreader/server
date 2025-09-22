package com.laundreader.userapi._core.security.jwt.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.laundreader.common.redis.RedisService;
import com.laundreader.domain.entity.user.User;
import com.laundreader.userapi._core.AppConstants;
import com.laundreader.userapi._core.security.jwt.JwtTokenProvider;
import com.laundreader.userapi._core.security.jwt.response.TokenResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtTokenService {
	private final JwtTokenProvider tokenProvider;
	private final RedisService redisService;

	public TokenResponse generateTokens(User user) {
		String accessToken = tokenProvider.generateAccessToken(user);
		String refreshToken = tokenProvider.generateRefreshToken();

		// Redis에 Refresh Token 저장
		saveRefreshToken(refreshToken, user.getEmail());

		return new TokenResponse(accessToken, refreshToken);
	}

	public void invalidateTokens(String accessToken, String refreshToken) {
		// accessToken 블랙리스트 등록
		addToBlacklist(accessToken);
		// refreshToken 삭제
		deleteRefreshToken(refreshToken);
	}

	public Optional<String> getEmailByRefreshToken(String refreshToken) {
		return Optional.ofNullable(
			redisService.getString(AppConstants.REDIS_REFRESH_TOKEN_PREFIX + refreshToken)
		);
	}

	private void saveRefreshToken(String refreshToken, String email) {
		redisService.setString(
			AppConstants.REDIS_REFRESH_TOKEN_PREFIX + refreshToken,
			email,
			AppConstants.REFRESH_TOKEN_EXP
		);
	}

	private void deleteRefreshToken(String refreshToken) {
		redisService.deleteKey(AppConstants.REDIS_REFRESH_TOKEN_PREFIX + refreshToken);
	}

	private void addToBlacklist(String token) {
		Long remainingMs = tokenProvider.getRemainingMs(token);
		redisService.addToBlacklist(token, remainingMs);
	}
}
