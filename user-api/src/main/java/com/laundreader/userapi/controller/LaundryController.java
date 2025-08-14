package com.laundreader.userapi.controller;

import com.laundreader.common.util.ApiUtils;
import com.laundreader.userapi.controller.dto.request.HamperSolutionRequest;
import com.laundreader.userapi.controller.dto.request.LabelAnalysisRequest;
import com.laundreader.userapi.controller.dto.request.SingleSolutionRequest;
import com.laundreader.userapi.service.LaundryService;
import com.laundreader.userapi.service.dto.response.HamperSolutionResponse;
import com.laundreader.userapi.service.dto.response.LabelAnalysisResponse;
import com.laundreader.userapi.service.dto.response.SingleSolutionResponse;
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
@RequestMapping("/user-api/")
@RequiredArgsConstructor
@Validated
@Slf4j
public class LaundryController {
    private final LaundryService laundryService;

    @PostMapping("/label-analysis")
    public ResponseEntity<ApiUtils.ApiResult<LabelAnalysisResponse>> labelAnalysis(
         @Valid @RequestBody LabelAnalysisRequest request
    ) {
        LabelAnalysisResponse response = laundryService.getLabelAnalysis(request.toImageDTO());
        return new ResponseEntity<>(ApiUtils.success(HttpStatus.OK, response), HttpStatus.OK);
    }

    @PostMapping("/laundry-solution/single")
    public ResponseEntity<ApiUtils.ApiResult<SingleSolutionResponse>> singleSolution(
            @Valid @RequestBody SingleSolutionRequest request
    ) {
        SingleSolutionResponse response;

        response = (request.getImage() == null)
                ? laundryService.getSingleSolution(request.toLaundryInfoDTO())
                : laundryService.getSingleSolution(request.toLaundryInfoDTO(), request.toImageDTO());

        return new ResponseEntity<>(ApiUtils.success(HttpStatus.OK, response), HttpStatus.OK);
    }

    @PostMapping("/laundry-solution/hamper")
    public ResponseEntity<ApiUtils.ApiResult<HamperSolutionResponse>> hamperSolution(
            @Valid @RequestBody HamperSolutionRequest request
    ) {
        HamperSolutionResponse response = laundryService.getHamperSolution(request.toHamperDTO());
        return new ResponseEntity<>(ApiUtils.success(HttpStatus.OK, response), HttpStatus.OK);
    }
}
