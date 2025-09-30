package com.laundreader.userapi.service.laundry;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laundreader.common.error.ErrorMessage;
import com.laundreader.common.error.exception.Exception400;
import com.laundreader.common.error.exception.Exception404;
import com.laundreader.common.error.exception.Exception500;
import com.laundreader.common.redis.RedisService;
import com.laundreader.common.util.Base64Extractor;
import com.laundreader.domain.dto.laundry.LaundrySymbolDTO;
import com.laundreader.domain.entity.laundry.Laundry;
import com.laundreader.domain.entity.user.User;
import com.laundreader.domain.repository.laundry.LaundryRepository;
import com.laundreader.external.NCPObjectStorage.NcpStorageService;
import com.laundreader.external.NCPObjectStorage.PresignedUrlCache;
import com.laundreader.external.clova.dto.LaundryAnalysisDTO;
import com.laundreader.external.clova.dto.SingleSolutionDTO;
import com.laundreader.external.clova.service.ClovaOcrService;
import com.laundreader.external.clova.service.ClovaStudioService;
import com.laundreader.userapi._core.AppConstants;
import com.laundreader.userapi.dto.image.ImageDTO;
import com.laundreader.userapi.dto.laundry.LaundryDTO;
import com.laundreader.userapi.dto.laundry.LaundryImageDTO;
import com.laundreader.userapi.response.laundry.LaundryAnalysisResponse;
import com.laundreader.userapi.response.laundry.LaundryGetResponse;
import com.laundreader.userapi.response.laundry.LaundrySaveResponse;
import com.laundreader.userapi.response.laundry.SingleSolutionResponse;
import com.laundreader.userapi.type.LaundrySymbolCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LaundryService {
	private final ClovaOcrService clovaOcrService;
	private final ClovaStudioService clovaStudioService;
	private final ObjectMapper objectMapper;
	private final NcpStorageService ncpStorageService;
	private final PresignedUrlCache presignedUrlCache;
	private final LaundryRepository laundryRepository;
	private final RedisService redisService;
	private final HamperService hamperService;

	public LaundryAnalysisResponse getLaundryAnalysis(ImageDTO labelImage, ImageDTO clothesImage) {
		// OCR 텍스트 추출
		String ocrText;
		try {
			ocrText = clovaOcrService.extractTextFromImage(
				labelImage.getFormat(),
				Base64Extractor.extractBase64PlainText(labelImage.getData())
			);
		} catch (IOException e) {
			throw new Exception500(ErrorMessage.ORC_REQUEST_FAILED);
		}

		// OCR 텍스트 + 의류 사진(선택) + 라벨 사진
		LaundryAnalysisDTO clovaResponse = clovaStudioService.laundryAnalysis(
			ocrText,
			labelImage.getData(),
			Optional.ofNullable(clothesImage).map(ImageDTO::getData).orElse(null)
		);

		// 유효하지 않은 심볼 코드 제거
		List<LaundrySymbolDTO> filteredSymbols = filterLaundrySymbols(clovaResponse.getLaundrySymbols());

		return new LaundryAnalysisResponse(
			clovaResponse.getMaterials(),
			clovaResponse.getColor(),
			clovaResponse.getType(),
			clovaResponse.getHasPrintOrTrims(),
			clovaResponse.getAdditionalInfo(),
			filteredSymbols
		);
	}

	// 단일 세탁 솔루션
	public SingleSolutionResponse getSingleSolution(LaundryDTO laundry) {
		String inputData = null;
		try {
			inputData = objectMapper.writeValueAsString(laundry);
		} catch (JsonProcessingException e) {
			log.error("단독 세탁 솔루션 input String 변환 실패: {}", e.getMessage());
			throw new Exception500(ErrorMessage.INTERNAL_ERROR);
		}

		SingleSolutionDTO clovaResponse = clovaStudioService.laundrySolutionSingle(
			inputData);

		return new SingleSolutionResponse(
			clovaResponse.getSolutions().stream()
				.map(s -> new SingleSolutionResponse.SolutionDTO(s.getName(), s.getContents()))
				.toList()
		);
	}

	@Transactional
	public LaundrySaveResponse saveLaundry(LaundryDTO dto, MultipartFile labelFile,
		MultipartFile clothesFile, User user) {

		String labelKey = null;
		String clothesKey = null;

		try {
			Laundry.LaundryBuilder builder = Laundry.builder()
				.materials(dto.getMaterials())
				.color(dto.getColor())
				.type(dto.getType())
				.hasPrintOrTrims(dto.getHasPrintOrTrims())
				.additionalInfo(dto.getAdditionalInfo())
				.laundrySymbols(dto.getLaundrySymbols())
				.solutions(dto.getSolutions())
				.user(user);

			// ----------------- 이미지 업로드 -----------------
			if (labelFile != null && !labelFile.isEmpty()) {
				labelKey = uploadLaundryImage(labelFile, user.getId(), "label");
				builder.labelImageKey(labelKey);
			}

			if (clothesFile != null && !clothesFile.isEmpty()) {
				clothesKey = uploadLaundryImage(clothesFile, user.getId(), "clothes");
				builder.clothesImageKey(clothesKey);
			}

			// 썸네일 결정 (clothes > label > null)
			String thumbnailKey = (clothesKey != null) ? clothesKey : labelKey;
			builder.thumbnailImageKey(thumbnailKey);

			// 1. Laundry 저장
			Laundry laundry = laundryRepository.save(builder.build());

			// 2. Hamper 캐시 갱신
			hamperService.updateHamperCache(user.getId());

			return new LaundrySaveResponse(laundry.getId());
		} catch (Exception e) {
			// 업로드된 파일이 있다면 삭제
			if (labelKey != null) {
				ncpStorageService.deleteFile(AppConstants.LAUNDRY_IMAGE_BUCKET_NAME, labelKey);
			}
			if (clothesKey != null) {
				ncpStorageService.deleteFile(AppConstants.LAUNDRY_IMAGE_BUCKET_NAME, clothesKey);
			}
			throw e; // 원래 예외 다시 던짐
		}
	}

	@Cacheable(value = "laundry", key = "#laundryId + ':' + #userId")
	public LaundryGetResponse getLaundry(Long laundryId, Long userId) {
		Laundry laundry = (Laundry)laundryRepository.findByIdAndUserId(laundryId, userId)
			.orElseThrow(() -> new Exception404("Laundry not found or not yours"));

		return LaundryGetResponse.toBuilderWithoutImage(laundry)
			.image(buildImageDTO(laundry))
			.build();
	}

	public void deleteLaundry(Long laundryId, Long userId) {
		Laundry laundry = (Laundry)laundryRepository.findByIdAndUserId(laundryId, userId)
			.orElseThrow(() -> new Exception404("Laundry not found or not yours"));

		deleteLaundryInternal(laundry);
	}

	@Transactional
	@CacheEvict(value = "hamper", key = "#laundry.user.id")
	public void deleteLaundryInternal(Laundry laundry) {
		// DB 삭제
		laundryRepository.delete(laundry);

		// 이미지 삭제
		List<String> fileKeys = List.of(
			laundry.getLabelImageKey(),
			laundry.getClothesImageKey()
		).stream().filter(Objects::nonNull).toList();

		for (String key : fileKeys) {
			try {
				ncpStorageService.deleteFile(AppConstants.LAUNDRY_IMAGE_BUCKET_NAME, key);
			} catch (Exception e) {
				redisService.appendToListLeft(AppConstants.LAUNDRY_DELETE_QUEUE, key);
				log.warn("Laundry 이미지 삭제 실패, Redis 큐에 적재: {}", key);
			}
		}
	}

	private List<LaundrySymbolDTO> filterLaundrySymbols(
		List<LaundryAnalysisDTO.Symbol> symbols) {
		Set<String> validCodes = LaundrySymbolCode.getValidCodes();

		return symbols == null ? List.of() :
			symbols.stream()
				.filter(d -> validCodes.contains(d.getCode()))
				.map(d -> LaundrySymbolDTO.builder()
					.code(d.getCode())
					.description(d.getDescription())
					.build()
				)
				.toList();
	}

	private String uploadLaundryImage(MultipartFile file, Long userId, String folder) {
		// 확장자 처리
		String originalFilename = file.getOriginalFilename();
		String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();

		String allowedExtensions = "jpeg";
		if (!allowedExtensions.equals(fileExtension)) {
			throw new Exception400("file", ErrorMessage.NOT_SUPPORTED_EXTENSION);
		}

		String key = "user-" + userId + "/" + folder + "/"
			+ UUID.randomUUID() + "." + fileExtension;

		ncpStorageService.uploadFile(AppConstants.LAUNDRY_IMAGE_BUCKET_NAME, file, key);
		return key;
	}

	private LaundryImageDTO buildImageDTO(Laundry laundry) {
		return LaundryImageDTO.builder()
			.label(getPresignedUrlWithCache(laundry.getLabelImageKey()))
			.clothes(getPresignedUrlWithCache(laundry.getClothesImageKey()))
			.build();
	}

	private String getPresignedUrlWithCache(String key) {
		if (key == null)
			return null;
		return presignedUrlCache.getOrGenerate(AppConstants.LAUNDRY_IMAGE_BUCKET_NAME, key,
			AppConstants.LAUNDRY_IMAGE_TTL);
	}
}
