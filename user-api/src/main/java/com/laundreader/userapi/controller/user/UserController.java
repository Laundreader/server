package com.laundreader.userapi.controller.user;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.laundreader.common.util.ApiUtils;
import com.laundreader.domain.entity.user.User;
import com.laundreader.userapi._core.AppConstants;
import com.laundreader.userapi._core.security.auth.PrincipalDetails;
import com.laundreader.userapi._core.security.jwt.service.JwtCookieService;
import com.laundreader.userapi.request.user.NicknameChangeRequest;
import com.laundreader.userapi.response.user.NicknameChangeResponse;
import com.laundreader.userapi.response.user.UserMeResponse;
import com.laundreader.userapi.service.user.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Validated
public class UserController {
	private final JwtCookieService jwtCookieService;
	private final UserService userService;

	@PreAuthorize("isAuthenticated()")
	@DeleteMapping
	public ResponseEntity<Void> withdraw(
		@AuthenticationPrincipal PrincipalDetails principal,
		@CookieValue(AppConstants.ACCESS_TOKEN_NAME_PREFIX) String accessToken,
		@CookieValue(AppConstants.REFRESH_TOKEN_NAME_PREFIX) String refreshToken
	) {
		// 서버단 탈퇴 및 토큰 무효화
		User user = principal.getUser();
		userService.withdraw(user, accessToken, refreshToken);

		// Max-Age=0으로 내려서 토큰 쿠키 무효화
		ResponseCookie accessTokenCookie = jwtCookieService.clearAccessTokenCookie();
		ResponseCookie refreshTokenCookie = jwtCookieService.clearRefreshTokenCookie();

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
		headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
		return new ResponseEntity<>(headers, HttpStatus.NO_CONTENT);
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/me")
	public ResponseEntity<ApiUtils.ApiResult> me(
		@AuthenticationPrincipal PrincipalDetails principal
	) {
		User user = principal.getUser();
		UserMeResponse response = userService.getUserMe(user);
		return new ResponseEntity<>(ApiUtils.success(response), HttpStatus.OK);
	}

	@PreAuthorize("isAuthenticated()")
	@PatchMapping("/nickname")
	public ResponseEntity<ApiUtils.ApiResult<NicknameChangeResponse>> changeNickname(
		@Valid @RequestBody NicknameChangeRequest request,
		@AuthenticationPrincipal PrincipalDetails principal
	) {
		User user = principal.getUser();
		NicknameChangeResponse response = userService.changeNickname(user, request.getNickname());
		return new ResponseEntity<>(ApiUtils.success(response), HttpStatus.OK);
	}
}
