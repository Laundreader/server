package com.laundreader.userapi._core;

public final class AppConstants {
	// JWT 관련
	public static final String ACCESS_TOKEN_NAME_PREFIX = "accessToken";
	public static final String REFRESH_TOKEN_NAME_PREFIX = "refreshToken";
	public static final long ACCESS_TOKEN_EXP = 1000L * 60 * 30;       // 30분
	public static final long REFRESH_TOKEN_EXP = 1000L * 60 * 60 * 24 * 30; // 30일
	// Redis 관련
	public static final String REDIS_REFRESH_TOKEN_PREFIX = "RT:";
	public static final String REDIS_BLACKLIST_PREFIX = "BL:";
	public static final String REDIS_BLACKLIST_VALUE_PREFIX = "BL";

	private AppConstants() {
	} // 인스턴스화 방지

}
