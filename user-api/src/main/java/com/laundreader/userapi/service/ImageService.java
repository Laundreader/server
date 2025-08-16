package com.laundreader.userapi.service;

import com.laundreader.external.clova.service.ClovaStudioService;
import com.laundreader.userapi.service.dto.ImageDTO;
import com.laundreader.userapi.service.response.image.ImageValidationResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {

    private final ClovaStudioService clovaStudioService;

    public ImageValidationResponseDTO validImageType(ImageDTO image) {
        boolean result =  clovaStudioService.imageAnalysis(image.getType().getValue(), image.getData());
        return new ImageValidationResponseDTO(result);
    }
}
