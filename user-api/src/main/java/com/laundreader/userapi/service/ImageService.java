package com.laundreader.userapi.service;

import org.springframework.stereotype.Service;

import com.laundreader.external.clova.service.ClovaStudioService;
import com.laundreader.userapi.service.dto.ImageDTO;
import com.laundreader.userapi.service.response.image.ImageValidationResponseDTO;
import com.laundreader.userapi.service.type.ImageType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {

	private final ClovaStudioService clovaStudioService;

	public ImageValidationResponseDTO validImageType(ImageType type, ImageDTO image) {
		boolean result = clovaStudioService.imageAnalysis(type.getValue(), image.getData());
		return new ImageValidationResponseDTO(result);
	}
}
