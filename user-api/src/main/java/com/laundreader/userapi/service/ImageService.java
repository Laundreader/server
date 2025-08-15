package com.laundreader.userapi.service;

import com.laundreader.external.clova.service.ClovaStudioService;
import com.laundreader.userapi.controller.dto.ImageDTO;
import com.laundreader.userapi.service.dto.response.image.ImageValidationResponse;
import com.laundreader.userapi.service.type.ImageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {

    private final ClovaStudioService clovaStudioService;

    public ImageValidationResponse validImageType(ImageDTO image) {
        boolean result =  clovaStudioService.imageAnalysis(image.getType().getValue(), image.getData());
        return new ImageValidationResponse(result);
    }
}
