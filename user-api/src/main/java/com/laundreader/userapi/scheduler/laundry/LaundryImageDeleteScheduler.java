package com.laundreader.userapi.scheduler.laundry;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.laundreader.common.redis.RedisService;
import com.laundreader.external.NCPObjectStorage.NcpStorageService;
import com.laundreader.userapi._core.AppConstants;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class LaundryImageDeleteScheduler {
	private final RedisService redisService;
	private final NcpStorageService ncpStorageService;

	@Scheduled(cron = "0 0 2 * * *") // 매일 새벽 2시에 실행
	public void processLaundryImageDeleteQueue() {
		String key;
		while ((key = redisService.rightPop(AppConstants.LAUNDRY_DELETE_QUEUE)) != null) {
			try {
				ncpStorageService.deleteFile(AppConstants.LAUNDRY_IMAGE_BUCKET_NAME, key);
				log.info("Redis 큐에서 Laundry 이미지 삭제 성공: {}", key);
			} catch (Exception e) {
				// 실패하면 다시 큐에 넣어 재시도
				redisService.appendToListLeft(AppConstants.LAUNDRY_DELETE_QUEUE, key);
				log.warn("Redis 큐에서 Laundry 이미지 삭제 재시도 실패, 다시 큐에 적재: {}", key);
			}
		}
	}
}
