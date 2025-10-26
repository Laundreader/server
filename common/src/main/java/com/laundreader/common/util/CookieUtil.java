package com.laundreader.common.util;

import org.springframework.boot.web.server.Cookie;
import org.springframework.http.ResponseCookie;

public class CookieUtil {

	public static ResponseCookie createCookie(String name, String value, long age) {
		return ResponseCookie.from(name, value)
			.domain(".laundreader.com") // 최상위 도메인
			.httpOnly(true) // 브라우저에서 접근 불가
			.secure(true) // https 환경에서만 쿠키가 발동
			.path("/")
			.sameSite(Cookie.SameSite.NONE.attributeValue())
			.maxAge(age)
			.build();
	}

	public static ResponseCookie deleteCookie(String name) {
		return createCookie(name, "", 0); // MaxAge=0 → 브라우저에서 즉시 삭제
	}

	/*
	 * localhost 개발을 위한 코드
	 * */
	public static ResponseCookie createLocalCookie(String name, String value, long age) {
		return ResponseCookie.from(name, value)
			.httpOnly(true) // 브라우저에서 접근 불가
			.secure(true) // https 환경에서만 쿠키가 발동
			.path("/")
			.sameSite(Cookie.SameSite.NONE.attributeValue())
			.maxAge(age)
			.build();
	}

	public static ResponseCookie deleteLocalCookie(String name) {
		return createLocalCookie(name, "", 0); // MaxAge=0 → 브라우저에서 즉시 삭제
	}
	/*
	 * localhost 개발을 위한 코드
	 * */
}