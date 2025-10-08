package com.laundreader.userapi._core.oauth.userInfo;

import java.util.List;
import java.util.Random;

public interface OAuth2UserInfo {
	String getEmail();

	// 랜덤 닉네임
	public default String getNickname() {
		return generateRandomNickname();
	}

	private String generateRandomNickname() {
		List<String> adjectives = List.of(
			"뽀송한", "상쾌한", "은밀한", "귀여운", "근면한",
			"게으른", "청량한", "깨끗한", "산뜻한", "활발한"
		);
		List<String> laundryNouns = List.of(
			"양말", "수건", "티셔츠", "청바지", "드럼통",
			"다리미", "세제", "집게", "옷걸이", "손수건"
		);

		Random random = new Random();
		String nickname;
		int attempts = 0;

		do {
			String adj = adjectives.get(random.nextInt(adjectives.size()));
			String laundry = laundryNouns.get(random.nextInt(laundryNouns.size()));

			nickname = adj + laundry;

			attempts++;
			// 혹시 6자 초과하면 재시도
		} while (nickname.length() > 6 && attempts < 6);

		// 6자 넘어가도 그냥 자름
		if (nickname.length() > 6) {
			nickname = nickname.substring(0, 6);
		}

		return nickname;
	}
}
