package com.laundreader.userapi.controller.laundry;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.laundreader.common.util.ApiUtils;
import com.laundreader.userapi.dto.image.ImageDTO;
import com.laundreader.userapi.request.laundry.HamperSolutionRequest;
import com.laundreader.userapi.request.laundry.LaundryAnalysisRequest;
import com.laundreader.userapi.request.laundry.SingleSolutionRequest;
import com.laundreader.userapi.response.laundry.HamperSolutionResponse;
import com.laundreader.userapi.response.laundry.LaundryAnalysisResponse;
import com.laundreader.userapi.response.laundry.SingleSolutionResponse;
import com.laundreader.userapi.service.laundry.LaundryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/user-api/laundry/")
@RequiredArgsConstructor
@Validated
@Slf4j
public class LaundryController {
	private final LaundryService laundryService;

	@PostMapping("/analysis")
	public ResponseEntity<ApiUtils.ApiResult<Map<String, LaundryAnalysisResponse>>> laundryAnalysis(
		@Valid @RequestBody LaundryAnalysisRequest request
	) {
		ImageDTO clothesImage = Optional.ofNullable(request.getClothes())
			.map(request::toImageDTO)
			.orElse(null);

		LaundryAnalysisResponse response = laundryService.getLaundryAnalysis(
			request.toImageDTO(request.getLabel()),
			clothesImage
		);

		// "laundry": { ... } 로 묶어서 보냄
		Map<String, LaundryAnalysisResponse> laundry = Map.of("laundry", response);
		return new ResponseEntity<>(ApiUtils.success(laundry), HttpStatus.OK);
	}

	@PostMapping("/solution/single")
	public ResponseEntity<ApiUtils.ApiResult<Map<String, SingleSolutionResponse>>> singleSolution(
		@Valid @RequestBody SingleSolutionRequest request
	) {
		SingleSolutionResponse response = laundryService.getSingleSolution(request.toLaundryDTO());
		// "laundry": { ... } 로 묶어서 보냄
		Map<String, SingleSolutionResponse> laundry = Map.of("laundry", response);
		return new ResponseEntity<>(ApiUtils.success(laundry), HttpStatus.OK);
	}

	@PostMapping("/solution/hamper")
	public ResponseEntity<ApiUtils.ApiResult<HamperSolutionResponse>> hamperSolution(
		@Valid @RequestBody HamperSolutionRequest request
	) {
		HamperSolutionResponse response = laundryService.getHamperSolution(request.toHamperDTO());
		return new ResponseEntity<>(ApiUtils.success(response), HttpStatus.OK);
	}
}
