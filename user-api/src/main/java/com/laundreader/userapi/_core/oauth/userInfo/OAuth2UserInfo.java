package com.laundreader.userapi._core.oauth.userInfo;

import java.util.List;
import java.util.Random;

public interface OAuth2UserInfo {
	String getEmail();

	String getAgeRange();

	String extractNickname();

	String getGender();

	// null-safe 닉네임 처리
	public default String getNickname() {
		String nickname = extractNickname();
		if (nickname != null) {
			return nickname;
		}
		return generateRandomNickname();
	}

	private String generateRandomNickname() {
		List<String> adjectives = List.of(
			"뽀송한", "상쾌한", "은밀한", "귀여운", "부지런한",
			"게으른", "청량한", "깨끗한", "산뜻한", "활발한"
		);
		List<String> laundryNouns = List.of(
			"양말", "수건", "티셔츠", "청바지", "드럼통",
			"다리미", "세제", "집게", "옷걸이", "손수건"
		);
		List<String> animals = List.of(
			"여우", "호랑이", "부엉이", "고양이", "강아지",
			"토끼", "곰", "펭귄", "사슴", "다람쥐"
		);

		Random random = new Random();
		String nickname;
		int attempts = 0;

		do {
			String adj = adjectives.get(random.nextInt(adjectives.size()));
			String laundry = laundryNouns.get(random.nextInt(laundryNouns.size()));
			String animal = animals.get(random.nextInt(animals.size()));

			nickname = adj + laundry + animal;

			attempts++;
			// 혹시 10자 초과하면 재시도
		} while (nickname.length() > 10 && attempts < 10);

		// 10자 넘어가도 그냥 자름
		if (nickname.length() > 10) {
			nickname = nickname.substring(0, 10);
		}

		return nickname;
	}
}
