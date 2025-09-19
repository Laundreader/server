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
			.path("/")
			.secure(true) // https 환경에서만 쿠키가 발동
			.sameSite(Cookie.SameSite.NONE.attributeValue())
			.httpOnly(true) // 브라우저에서 접근 불가
			.build();
	}
}