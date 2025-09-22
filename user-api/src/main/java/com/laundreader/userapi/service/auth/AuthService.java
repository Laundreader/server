package com.laundreader.userapi.service.auth;

import org.springframework.stereotype.Service;

import com.laundreader.common.error.exception.Exception400;
import com.laundreader.common.error.exception.Exception404;
import com.laundreader.domain.entity.user.User;
import com.laundreader.domain.repository.user.UserRepository;
import com.laundreader.domain.type.user.UserStatus;
import com.laundreader.userapi._core.security.jwt.JwtTokenProvider;
import com.laundreader.userapi._core.security.jwt.response.TokenResponse;
import com.laundreader.userapi._core.security.jwt.service.JwtTokenService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final UserRepository userRepository;
	private final JwtTokenProvider tokenProvider;
	private final JwtTokenService tokenService;

	public TokenResponse reissue(String oldAccessToken, String oldRefreshToken) {
		// refreshToken 유효성 검사 (만료)
		try {
			tokenProvider.isTokenValid(oldRefreshToken);
		} catch (Exception e) {
			throw new Exception400("Invalid refresh token: ", e.getMessage());
		}

		// Redis에서 이메일 조회
		String email = tokenService.getEmailByRefreshToken(oldRefreshToken)
			.orElseThrow(() -> new Exception400("Invalid refresh token: ", "not found in Redis"));

		// 기존 토큰 무효화
		tokenService.invalidateTokens(oldAccessToken, oldRefreshToken);

		// 사용자 로드
		User user = userRepository.findByEmailAndStatus(email, UserStatus.ACTIVE)
			.orElseThrow(() -> new Exception404("User not found"));

		// Token 발급  (새 Access + Refresh Token 생성 및 Redis 저장)
		return tokenService.generateTokens(user);
	}

	public void logout(String accessToken, String refreshToken) {
		tokenService.invalidateTokens(accessToken, refreshToken);
	}
}
