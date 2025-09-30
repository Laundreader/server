package com.laundreader.userapi.service.user;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.laundreader.domain.entity.laundry.Laundry;
import com.laundreader.domain.entity.user.User;
import com.laundreader.domain.entity.user.UserOAuthToken;
import com.laundreader.domain.entity.withdrawLog.WithdrawLog;
import com.laundreader.domain.repository.laundry.LaundryRepository;
import com.laundreader.domain.repository.user.UserOAuthTokenRepository;
import com.laundreader.domain.repository.user.UserRepository;
import com.laundreader.domain.repository.withdrawLog.WithdrawLogRepository;
import com.laundreader.external.naverOAuth.NaverTokenService;
import com.laundreader.userapi._core.security.jwt.service.JwtTokenService;
import com.laundreader.userapi.response.user.UserMeResponse;
import com.laundreader.userapi.service.laundry.LaundryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final UserOAuthTokenRepository oAuthTokenRepository;
	private final LaundryRepository laundryRepository;
	private final WithdrawLogRepository withdrawLogRepository;
	private final LaundryService laundryService;
	private final JwtTokenService tokenService;
	private final NaverTokenService naverTokenService;
	private final ApplicationEventPublisher applicationEventPublisher;

	@Transactional
	public void withdraw(User user, String accessToken, String refreshToken) {
		// 1. 소셜 로그인 연결 끊기
		UserOAuthToken oAuthToken = user.getOAuthToken();
		if (oAuthToken != null) {
			String oAuthAccessToken = oAuthToken.getAccessToken();

			// 토큰 만료 시 재발급
			if (oAuthToken.getAccessTokenExpireAt().isBefore(LocalDateTime.now())) {
				Map<String, String> refreshed = naverTokenService.refresh(oAuthToken.getRefreshToken());
				oAuthAccessToken = refreshed.get("access_token");
			}

			// 연결 끊기 및 DB 삭제
			if (naverTokenService.unlink(oAuthAccessToken)) {
				oAuthTokenRepository.delete(oAuthToken);
			}
		}

		// 2. 유저 빨래 전부 삭제 (이미지 + Hamper 캐시 포함)
		List<Laundry> laundries = laundryRepository.findAllByUserId(user.getId());
		laundries.forEach(laundryService::deleteLaundryInternal);

		// 3. 유저 삭제 추후 status 수정으로 기획 변경될 수 있음
		userRepository.delete(user);

		// 4. 탈퇴 로그 저장
		withdrawLogRepository.save(new WithdrawLog(user.getEmail(), "사용자 요청"));

		// 이벤트 발행 → DB commit 이후 토큰 무효화
		applicationEventPublisher.publishEvent(
			new UserWithdrawnEvent(user.getEmail(), accessToken, refreshToken)
		);
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleWithdraw(UserWithdrawnEvent event) {
		tokenService.invalidateTokens(event.accessToken(), event.refreshToken());
	}

	public UserMeResponse getUserMe(User user) {
		return new UserMeResponse(user.getEmail(), user.getProvider().name(), user.getNickname());
	}

	public record UserWithdrawnEvent(String email, String accessToken, String refreshToken) {
	}
}
