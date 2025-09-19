package com.laundreader.userapi._core.oauth.userInfo;

import java.util.Map;

public class NaverUserInfo implements OAuth2UserInfo {
	private final Map<String, Object> attributes;

	public NaverUserInfo(Map<String, Object> attributes) {
		this.attributes = (Map<String, Object>)attributes.get("response");
	}

	@Override
	public String getEmail() {
		return (String)attributes.get("email");
	}

	@Override
	public String getAgeRange() {
		return (String)attributes.get("age");
	}

	@Override
	public String extractNickname() {
		return (String)attributes.get("nickname");
	}

	@Override
	public String getGender() {
		return (String)attributes.get("gender");
	}
}

