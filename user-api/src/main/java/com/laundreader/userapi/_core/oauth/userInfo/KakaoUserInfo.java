package com.laundreader.userapi._core.oauth.userInfo;

import java.util.Map;

public class KakaoUserInfo implements OAuth2UserInfo {
	private final Map<String, Object> attributes;

	public KakaoUserInfo(Map<String, Object> attributes) {
		this.attributes = (Map<String, Object>)attributes.get("kakao_account");
	}

	@Override
	public String getEmail() {
		return (String)attributes.get("email");
	}

	@Override
	public String getAgeRange() {
		return (String)attributes.get("age_range");
	}

	@Override
	public String extractNickname() {
		Map<String, Object> profile = (Map<String, Object>)attributes.get("profile");
		return profile != null ? (String)profile.get("nickname") : null;
	}

	@Override
	public String getGender() {
		return (String)attributes.get("gender");
	}
}
