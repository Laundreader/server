package com.laundreader.userapi._core.oauth.userInfo;

import java.util.Map;

public class OAuth2UserInfoFactory {
	public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
		return switch (registrationId.toLowerCase()) {
			case "naver" -> new NaverUserInfo(attributes);
			// case "kakao" -> new KakaoUserInfo(attributes);
			default -> throw new IllegalArgumentException("Unsupported provider: " + registrationId);
		};
	}
}

