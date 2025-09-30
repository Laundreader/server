package com.laundreader.external.naverOAuth;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.laundreader.common.error.exception.Exception500;
import com.laundreader.common.util.JsonConverter;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NaverTokenService {

	private static final String NID_BASE_URL = "https://nid.naver.com";

	private final WebClient.Builder webClientBuilder;
	private final JsonConverter jsonConverter;

	@Value("${oauth2.naver.client.id}")
	private String clientId;
	@Value("${oauth2.naver.client.secret}")
	private String clientSecret;

	public Map<String, String> refresh(String refreshToken) {
		WebClient webClient = webClientBuilder.baseUrl(NID_BASE_URL).build();

		try {
			String responseBody = webClient.get()
				.uri(uriBuilder -> uriBuilder
					.path("/oauth2.0/token")
					.queryParam("grant_type", "refresh_token")
					.queryParam("client_id", clientId)
					.queryParam("client_secret", clientSecret)
					.queryParam("refresh_token", refreshToken)
					.build())
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(String.class)
				.block();

			// JSON 파싱
			Map<String, Object> result = jsonConverter.jsonToCollection(
				responseBody,
				new TypeReference<Map<String, Object>>() {
				}
			);

			// access_token과 expires_in만 반환
			return Map.of(
				"access_token", (String)result.get("access_token"),
				"expires_in", String.valueOf(result.get("expires_in"))
			);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception500("naver token refresh failed...");
		}

	}

	public boolean unlink(String accessToken) {
		WebClient webClient = webClientBuilder.baseUrl(NID_BASE_URL).build();

		try {
			String responseBody = webClient.get()
				.uri(uriBuilder -> uriBuilder
					.path("/oauth2.0/token")
					.queryParam("grant_type", "delete")
					.queryParam("client_id", clientId)
					.queryParam("client_secret", clientSecret)
					.queryParam("access_token", accessToken)
					.build())
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(String.class)
				.block();

			return responseBody != null && responseBody.contains("\"result\":\"success\"");

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
