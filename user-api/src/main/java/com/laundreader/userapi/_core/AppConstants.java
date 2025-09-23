package com.laundreader.userapi._core;

public final class AppConstants {
	// JWT 관련
	public static final String ACCESS_TOKEN_NAME_PREFIX = "accessToken";
	public static final String REFRESH_TOKEN_NAME_PREFIX = "refreshToken";
	public static final long ACCESS_TOKEN_EXP = 1000L * 60 * 30;       // 30분
	public static final long REFRESH_TOKEN_EXP = 1000L * 60 * 60 * 24 * 30; // 30일
	public static final String REDIS_REFRESH_TOKEN_PREFIX = "refresh_token:";

	// Object Storage 관련
	public static final String LAUNDRY_IMAGE_BUCKET_NAME = "laundreader-laundry-images";

	private AppConstants() {
	} // 인스턴스화 방지

}
