package com.laundreader.userapi._core.oauth;

import java.util.Map;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.laundreader.domain.user.entity.User;
import com.laundreader.domain.user.repository.UserRepository;
import com.laundreader.domain.user.type.Provider;
import com.laundreader.domain.user.type.UserStatus;
import com.laundreader.userapi._core.oauth.userInfo.OAuth2UserInfo;
import com.laundreader.userapi._core.oauth.userInfo.OAuth2UserInfoFactory;
import com.laundreader.userapi._core.security.auth.PrincipalDetails;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

	private final UserRepository userRepository;

	@Transactional
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);
		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		Map<String, Object> attributes = oAuth2User.getAttributes();
		OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, attributes);

		// 이메일, 연령대 필수
		// 닉네임, 성별 선택
		String email = userInfo.getEmail();
		String ageRange = userInfo.getAgeRange();
		String nickname = userInfo.getNickname();
		String gender = userInfo.getGender();

		// 필수 정보 체크
		if (email == null || ageRange == null) {
			throw new OAuth2AuthenticationException(
				new OAuth2Error("502"),
				"Bad Gateway: 필수 정보(이메일, 연령대)가 없습니다."
			);
		}

		// DB 저장/조회
		User user = userRepository.findByEmailAndStatus(email, UserStatus.ACTIVE)
			.map(u -> {
				// 이미 다른 provider로 가입된 경우
				if (!u.getProvider().name().equalsIgnoreCase(registrationId)) {
					throw new OAuth2AuthenticationException(
						new OAuth2Error("409"),
						"이미 연결된 계정이 있습니다."
					);
				}
				return u;
			})
			.orElseGet(() -> {
				User newUser = User.builder()
					.email(email)
					.ageRange(ageRange)
					.nickname(nickname)
					.gender(gender)
					.provider(Provider.valueOf(registrationId.toUpperCase()))
					.build();
				return userRepository.save(newUser);
			});

		return new PrincipalDetails(user, attributes);
	}
}

