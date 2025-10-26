package com.laundreader.external.NCPObjectStorage;

import org.springframework.stereotype.Component;

import com.laundreader.common.redis.RedisService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PresignedUrlCache {

	private final NcpStorageService ncpStorageService;
	private final RedisService redisService;

	public String getOrGenerate(String bucketName, String s3Key, long expirationSeconds) {
		String cachedUrl = redisService.getString(s3Key);
		if (cachedUrl != null)
			return cachedUrl;

		String url = ncpStorageService.generateGetPresignedUrl(bucketName, s3Key, expirationSeconds);
		// presigned URL 전체 유효시간의 50%만 캐싱
		long cacheTTL = (long)(expirationSeconds * 1000 * 0.5);
		redisService.setString(s3Key, url, cacheTTL);
		return url;
	}
}