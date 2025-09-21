package com.laundreader.userapi.service.user;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.laundreader.domain.user.entity.User;
import com.laundreader.domain.user.repository.UserRepository;
import com.laundreader.domain.withdrawLog.entity.WithdrawLog;
import com.laundreader.domain.withdrawLog.repository.WithdrawLogRepository;
import com.laundreader.userapi._core.security.jwt.service.JwtTokenService;
import com.laundreader.userapi.response.user.UserMeResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final WithdrawLogRepository withdrawLogRepository;
	private final JwtTokenService tokenService;
	private final ApplicationEventPublisher applicationEventPublisher;

	@Transactional
	public void withdraw(User user, String accessToken, String refreshToken) {
		// @Todo 유저 컨텐츠(사진, 빨래 바구니) 삭제, cascade = CascadeType.REMOVE 설정

		// 추후 status 수정으로 기획 변경될 수 있음
		userRepository.delete(user);

		// 탈퇴 로그 저장
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
