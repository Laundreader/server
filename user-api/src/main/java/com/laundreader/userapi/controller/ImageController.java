package com.laundreader.userapi.controller;

import com.laundreader.common.util.ApiUtils;
import com.laundreader.userapi.controller.request.image.ImageValidationRequest;
import com.laundreader.userapi.service.ImageService;
import com.laundreader.userapi.service.response.image.ImageValidationResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user-api/image")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/validation")
    public ResponseEntity<ApiUtils.ApiResult<ImageValidationResponseDTO>> imageValidation(
            @Valid @RequestBody ImageValidationRequest request
    ) {
        ImageValidationResponseDTO response = imageService.validImageType(request.toImageDTO());
        return new ResponseEntity<>(ApiUtils.success(response), HttpStatus.OK);
    }
}
