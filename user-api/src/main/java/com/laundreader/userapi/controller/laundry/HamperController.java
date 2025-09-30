package com.laundreader.userapi.controller.laundry;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.laundreader.common.util.ApiUtils;
import com.laundreader.domain.entity.user.User;
import com.laundreader.userapi._core.security.auth.PrincipalDetails;
import com.laundreader.userapi.response.laundry.HamperGetResponse;
import com.laundreader.userapi.service.laundry.HamperService;

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
		HamperGetResponse hamper = hamperService.getHamper(user.getId());
		return new ResponseEntity<>(ApiUtils.success(hamper.getHamper().isEmpty() ? null : hamper), HttpStatus.OK);
	}
}
