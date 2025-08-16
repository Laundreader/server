package com.laundreader.userapi.controller;

import com.laundreader.common.util.ApiUtils;
import com.laundreader.userapi.service.dto.ImageDTO;
import com.laundreader.userapi.controller.request.laundry.HamperSolutionRequest;
import com.laundreader.userapi.controller.request.laundry.LaundryAnalysisRequest;
import com.laundreader.userapi.controller.request.laundry.SingleSolutionRequest;
import com.laundreader.userapi.service.LaundryService;
import com.laundreader.userapi.service.response.laundry.HamperSolutionResponseDTO;
import com.laundreader.userapi.service.response.laundry.LaundryAnalysisResponseDTO;
import com.laundreader.userapi.service.response.laundry.SingleSolutionResponseDTO;
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

import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/user-api/laundry/")
@RequiredArgsConstructor
@Validated
@Slf4j
public class LaundryController {
    private final LaundryService laundryService;

    @PostMapping("/analysis")
    public ResponseEntity<ApiUtils.ApiResult<Map<String, LaundryAnalysisResponseDTO>>> laundryAnalysis(
         @Valid @RequestBody LaundryAnalysisRequest request
    ) {
        ImageDTO clothesImage = Optional.ofNullable(request.getClothesImage())
                .map(request::toImageDTO)
                .orElse(null);

        LaundryAnalysisResponseDTO response = laundryService.getLaundryAnalysis(
                request.toImageDTO(request.getLabelImage()),
                clothesImage
        );

        // "laundry": { ... } 로 묶어서 보냄
        Map<String, LaundryAnalysisResponseDTO> laundry = Map.of("laundry", response);
        return new ResponseEntity<>(ApiUtils.success(laundry), HttpStatus.OK);
    }

    @PostMapping("/solution/single")
    public ResponseEntity<ApiUtils.ApiResult<Map<String, SingleSolutionResponseDTO>>> singleSolution(
            @Valid @RequestBody SingleSolutionRequest request
    ) {
        SingleSolutionResponseDTO response = laundryService.getSingleSolution(request.toLaundryDTO());
        // "laundry": { ... } 로 묶어서 보냄
        Map<String, SingleSolutionResponseDTO> laundry = Map.of("laundry", response);
        return new ResponseEntity<>(ApiUtils.success(laundry), HttpStatus.OK);
    }

    @PostMapping("/solution/hamper")
    public ResponseEntity<ApiUtils.ApiResult<HamperSolutionResponseDTO>> hamperSolution(
            @Valid @RequestBody HamperSolutionRequest request
    ) {
        HamperSolutionResponseDTO response = laundryService.getHamperSolution(request.toHamperDTO());
        return new ResponseEntity<>(ApiUtils.success(response), HttpStatus.OK);
    }
}
