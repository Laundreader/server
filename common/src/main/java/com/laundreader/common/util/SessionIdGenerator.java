package com.laundreader.common.util;

import java.security.SecureRandom;
import java.util.Base64;

public class SessionIdGenerator {
	private static final SecureRandom secureRandom = new SecureRandom();
	private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder().withoutPadding();

	public static String generateSessionId(int length) {
		byte[] randomBytes = new byte[length];
		secureRandom.nextBytes(randomBytes);
		return base64Encoder.encodeToString(randomBytes);
	}
}
