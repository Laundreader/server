package com.laundreader.userapi.controller.laundry;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.laundreader.common.util.ApiUtils;
import com.laundreader.domain.entity.user.User;
import com.laundreader.userapi._core.security.auth.PrincipalDetails;
import com.laundreader.userapi.dto.image.ImageDTO;
import com.laundreader.userapi.request.laundry.LaundryAnalysisRequest;
import com.laundreader.userapi.request.laundry.LaundrySaveRequest;
import com.laundreader.userapi.request.laundry.SingleSolutionRequest;
import com.laundreader.userapi.response.laundry.LaundryAnalysisResponse;
import com.laundreader.userapi.response.laundry.LaundryGetResponse;
import com.laundreader.userapi.response.laundry.LaundrySaveResponse;
import com.laundreader.userapi.response.laundry.SingleSolutionResponse;
import com.laundreader.userapi.service.laundry.LaundryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class LaundryController {
	private final LaundryService laundryService;

	@PostMapping("/public/laundry/analysis")
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

	@PostMapping("/public/laundry/solution")
	public ResponseEntity<ApiUtils.ApiResult<Map<String, SingleSolutionResponse>>> singleSolution(
		@Valid @RequestBody SingleSolutionRequest request
	) {
		SingleSolutionResponse response = laundryService.getSingleSolution(request.toLaundryDTO());
		// "laundry": { ... } 로 묶어서 보냄
		Map<String, SingleSolutionResponse> laundry = Map.of("laundry", response);
		return new ResponseEntity<>(ApiUtils.success(laundry), HttpStatus.OK);
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/laundry")
	public ResponseEntity<ApiUtils.ApiResult<LaundrySaveResponse>> saveLaundry(
		@Valid @RequestPart("laundry") LaundrySaveRequest request,
		@RequestPart(value = "label", required = false) MultipartFile labelFile,
		@RequestPart(value = "clothes", required = false) MultipartFile clothesFile,
		@AuthenticationPrincipal PrincipalDetails principal
	) {
		User user = principal.getUser();
		LaundrySaveResponse response = laundryService.saveLaundry(request.toLaundryDTO(), labelFile, clothesFile, user);
		return new ResponseEntity<>(ApiUtils.success(response), HttpStatus.OK);
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/laundry/{id}")
	public ResponseEntity<ApiUtils.ApiResult<LaundryGetResponse>> getLaundry(
		@PathVariable("id") Long laundryId,
		@AuthenticationPrincipal PrincipalDetails principal
	) {
		User user = principal.getUser();
		LaundryGetResponse response = laundryService.getLaundry(laundryId, user.getId());
		return new ResponseEntity<>(ApiUtils.success(response), HttpStatus.OK);
	}

	@PreAuthorize("isAuthenticated()")
	@DeleteMapping("/laundry/{id}")
	public ResponseEntity<Void> deleteLaundry(
		@PathVariable("id") Long laundryId,
		@AuthenticationPrincipal PrincipalDetails principal
	) {
		User user = principal.getUser();
		laundryService.deleteLaundry(laundryId, user.getId());
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
