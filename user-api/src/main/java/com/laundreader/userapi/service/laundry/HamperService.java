package com.laundreader.userapi.service.laundry;

import java.util.List;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laundreader.common.redis.RedisService;
import com.laundreader.domain.entity.laundry.Laundry;
import com.laundreader.domain.repository.laundry.LaundryRepository;
import com.laundreader.external.NCPObjectStorage.PresignedUrlCache;
import com.laundreader.external.clova.service.ClovaOcrService;
import com.laundreader.external.clova.service.ClovaStudioService;
import com.laundreader.userapi._core.AppConstants;
import com.laundreader.userapi.response.laundry.HamperGetResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class HamperService {
	private final ClovaOcrService clovaOcrService;
	private final ClovaStudioService clovaStudioService;
	private final ObjectMapper objectMapper;
	private final PresignedUrlCache presignedUrlCache;
	private final LaundryRepository laundryRepository;
	private final RedisService redisService;

	@Cacheable(value = "hamper", key = "#userId")
	public HamperGetResponse getHamper(Long userId) {
		return buildHamper(userId);
	}

	@CachePut(value = "hamper", key = "#userId")
	public HamperGetResponse updateHamperCache(Long userId) {
		return buildHamper(userId);
	}

	private HamperGetResponse buildHamper(Long userId) {
		List<Laundry> laundry = laundryRepository.findAllByUser_Id(userId);

		List<HamperGetResponse.HamperLaundryDTO> hamperItems = laundry.stream()
			.map(l -> new HamperGetResponse.HamperLaundryDTO(l.getId(),
				getPresignedUrlWithCache(l.getThumbnailImageKey())))
			.toList();

		return new HamperGetResponse(hamperItems);
	}

	private String getPresignedUrlWithCache(String key) {
		if (key == null)
			return null;
		return presignedUrlCache.getOrGenerate(AppConstants.LAUNDRY_IMAGE_BUCKET_NAME, key,
			AppConstants.LAUNDRY_IMAGE_TTL);
	}

}
