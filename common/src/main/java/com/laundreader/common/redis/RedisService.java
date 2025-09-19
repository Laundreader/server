package com.laundreader.common.redis;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.laundreader.common.util.JsonConverter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisService {
	public static final String REDIS_BLACKLIST_PREFIX = "B_LIST:";
	private final RedisTemplate<String, String> redisTemplate;
	private final JsonConverter jsonConverter;

	public <T> T getObject(String key, Class<T> clazz) {
		String json = redisTemplate.opsForValue().get(key);
		if (json == null || json.isEmpty())
			return null;
		return jsonConverter.jsonToObject(json, clazz);
	}

	public void setObject(String key, Object obj, long timeoutMs) {
		redisTemplate.opsForValue().set(key, jsonConverter.objectToJson(obj), Duration.ofMillis(timeoutMs));
	}

	public void setString(String key, String value, long timeoutMs) {
		redisTemplate.opsForValue().set(key, value, Duration.ofMillis(timeoutMs));
	}

	public String getString(String key) {
		return redisTemplate.opsForValue().get(key);
	}

	public List<String> getList(String key) {
		return Optional.ofNullable(redisTemplate.opsForList().range(key, 0, -1))
			.orElse(new ArrayList<>());
	}

	public void appendToList(String key, String value) {
		redisTemplate.opsForList().rightPush(key, value);
	}

	public boolean hasKey(String key) {
		return Boolean.TRUE.equals(redisTemplate.hasKey(key));
	}

	public boolean deleteKey(String key) {
		return Boolean.TRUE.equals(redisTemplate.delete(key));
	}

	public void addToBlacklist(String key, long expireMs) {
		redisTemplate.opsForValue().set(REDIS_BLACKLIST_PREFIX + key, "", Duration.ofMillis(expireMs));
	}

	public boolean isBlacklisted(String key) {
		return Boolean.TRUE.equals(hasKey(REDIS_BLACKLIST_PREFIX + key));
	}
}
