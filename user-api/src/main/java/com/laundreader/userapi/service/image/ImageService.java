package com.laundreader.userapi.service.image;

import org.springframework.stereotype.Service;

import com.laundreader.external.clova.service.ClovaStudioService;
import com.laundreader.userapi.dto.image.ImageDTO;
import com.laundreader.userapi.response.image.ImageValidationResponse;
import com.laundreader.userapi.type.ImageType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {

	private final ClovaStudioService clovaStudioService;

	public ImageValidationResponse validImageType(ImageType type, ImageDTO image) {
		boolean result = clovaStudioService.imageAnalysis(type.getValue(), image.getData());
		return new ImageValidationResponse(result);
	}
}
