package com.laundreader.userapi.controller.laundry;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.laundreader.common.util.ApiUtils;
import com.laundreader.domain.entity.user.User;
import com.laundreader.userapi._core.security.auth.PrincipalDetails;
import com.laundreader.userapi.request.laundry.HamperSolutionRequest;
import com.laundreader.userapi.response.laundry.HamperGetResponse;
import com.laundreader.userapi.response.laundry.HamperSolutionResponse;
import com.laundreader.userapi.service.laundry.HamperService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/hamper")
@RequiredArgsConstructor
@Validated
public class HamperController {
	private final HamperService hamperService;

	@PreAuthorize("isAuthenticated()")
	@GetMapping
	public ResponseEntity<ApiUtils.ApiResult<HamperGetResponse>> getHamper(
		@AuthenticationPrincipal PrincipalDetails principal
	) {
		User user = principal.getUser();
		HamperGetResponse response = hamperService.getHamper(user.getId());
		return new ResponseEntity<>(ApiUtils.success(response), HttpStatus.OK);
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/solution")
	public ResponseEntity<ApiUtils.ApiResult<HamperSolutionResponse>> hamperSolution(
		@Valid @RequestBody HamperSolutionRequest request,
		@AuthenticationPrincipal PrincipalDetails principal
	) {
		User user = principal.getUser();
		HamperSolutionResponse response = hamperService.getHamperSolution(request.getLaundryIds(), user.getId());
		return new ResponseEntity<>(ApiUtils.success(response), HttpStatus.OK);
	}
}
