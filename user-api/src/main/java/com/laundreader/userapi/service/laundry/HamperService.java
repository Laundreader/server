package com.laundreader.userapi.service.laundry;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laundreader.common.error.ErrorMessage;
import com.laundreader.common.error.exception.Exception404;
import com.laundreader.common.error.exception.Exception500;
import com.laundreader.domain.entity.laundry.Laundry;
import com.laundreader.domain.repository.laundry.LaundryRepository;
import com.laundreader.external.NCPObjectStorage.PresignedUrlCache;
import com.laundreader.external.clova.dto.HamperSolutionDTO;
import com.laundreader.external.clova.service.ClovaStudioService;
import com.laundreader.userapi._core.AppConstants;
import com.laundreader.userapi.dto.laundry.HamperDTO;
import com.laundreader.userapi.response.laundry.HamperGetResponse;
import com.laundreader.userapi.response.laundry.HamperSolutionResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class HamperService {
	private final ClovaStudioService clovaStudioService;
	private final ObjectMapper objectMapper;
	private final PresignedUrlCache presignedUrlCache;
	private final LaundryRepository laundryRepository;

	@Cacheable(value = "hamper", key = "#userId")
	public HamperGetResponse getHamper(Long userId) {
		return buildHamper(userId);
	}

	@CachePut(value = "hamper", key = "#userId")
	public HamperGetResponse updateHamperCache(Long userId) {
		return buildHamper(userId);
	}

	public HamperSolutionResponse getHamperSolution(List<Long> laundryIds, Long userId) {
		List<Laundry> laundries = laundryRepository.findAllByIdInAndUserId(laundryIds, userId);

		/*
		 * 존재하지 않거나 본인 소유가 아닌 세탁물 필터링
		 * */
		Set<Long> foundIds = laundries.stream()
			.map(Laundry::getId)
			.collect(Collectors.toSet());

		Set<Long> requestedIds = new HashSet<>(laundryIds);
		requestedIds.removeAll(foundIds);

		if (!requestedIds.isEmpty()) {
			List<Long> missingIds = requestedIds.stream()
				.sorted()
				.toList();
			throw new Exception404("Laundry not found or not yours: " + missingIds);
		}

		/*
		 * 솔루션 생성
		 * */
		HamperDTO hamper = HamperDTO.fromEntities(laundries);
		String inputData = null;
		try {
			inputData = objectMapper.writeValueAsString(hamper);
		} catch (JsonProcessingException e) {
			log.error("빨래바구니 세탁 솔루션 input String 변환 실패: {}", e.getMessage());
			throw new Exception500(ErrorMessage.INTERNAL_ERROR);
		}

		HamperSolutionDTO clovaResponse = clovaStudioService.laundrySolutionHamper(
			inputData);

		/*
		 * 응답 생성
		 * */
		Map<Long, Laundry> laundryMap = laundries.stream()
			.collect(Collectors.toMap(Laundry::getId, Function.identity()));

		return new HamperSolutionResponse(
			clovaResponse.getGroups().stream()
				.map(g -> new HamperSolutionResponse.groupDTO(
					g.getId(),
					g.getName(),
					g.getSolution(),
					getLaundries(laundryMap, g.getLaundryIds())
				))
				.toList()
		);
	}

	private List<HamperSolutionResponse.SolutionLaundryDTO> getLaundries(Map<Long, Laundry> laundryMap,
		List<Integer> laundryIds) {
		return laundryIds.stream()
			.map(Long::valueOf)
			.sorted()
			.map(laundryMap::get) // 없으면 null
			.filter(Objects::nonNull) // 혹시 null이면 필터링
			.map(l -> new HamperSolutionResponse.SolutionLaundryDTO(
				l.getId(),
				getPresignedUrlWithCache(l.getThumbnailImageKey())
			))
			.toList();
	}

	private HamperGetResponse buildHamper(Long userId) {
		List<Laundry> laundry = laundryRepository.findAllByUserIdOrderByCreatedAtDesc(userId);

		List<HamperGetResponse.HamperLaundryDTO> hamperItems = laundry.stream()
			.map(l -> new HamperGetResponse.HamperLaundryDTO(
				l.getId(),
				getPresignedUrlWithCache(l.getThumbnailImageKey()))
			)
			.toList();

		return new HamperGetResponse(hamperItems);
	}

	private String getPresignedUrlWithCache(String key) {
		if (key == null)
			return null;
		return presignedUrlCache.getOrGenerate(AppConstants.LAUNDRY_IMAGE_BUCKET_NAME, key,
			AppConstants.LAUNDRY_THUMBNAIL_TTL);
	}

}
