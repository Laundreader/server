package com.laundreader.userapi._core.oauth.userInfo;

public interface OAuth2UserInfo {
	String getEmail();

	String getAgeRange();

	String extractNickname();

	String getGender();

	// null-safe 닉네임 처리
	public default String getNickname() {
		String nickname = extractNickname();
		return nickname != null ? nickname : "익명"; // null이면 기본값
	}
}
