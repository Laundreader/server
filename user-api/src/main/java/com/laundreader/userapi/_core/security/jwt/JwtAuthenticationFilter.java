package com.laundreader.userapi._core.security.jwt;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.laundreader.common.redis.RedisService;
import com.laundreader.userapi._core.AppConstants;
import com.laundreader.userapi._core.security.auth.PrincipalDetails;
import com.laundreader.userapi._core.security.auth.PrincipalDetailsService;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/*
 * 1. JwtAuthenticationFilter 인증 처리 필터
 * 		- JWT 토큰을 검증 하고 사용자를 인증함
 * 		- JWT 토큰 파싱 -> 서명 유효성 검사 -> 만료 시간 검사 -> 사용자 인증
 *
 * 2. JwtAuthorizationFilter 인가 처리 필터
 * 		- 인증된 사용자의 요청이 접근 권한을 가지고 있는지 확인
 * 		- JWT 토큰 파싱 -> 사용자 식별 -> 권한 검사 -> 접근 제어
 *
 * Spring Security 설정 에서 접근 제어 설정( authorizeRequests() )을 했기 때문에
 *  JwtAuthenticationFilter 만 커스텀 한다.
 * */

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtTokenProvider jwtTokenProvider;
	private final RedisService redisService;
	private final PrincipalDetailsService principalDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		// 1. 쿠키에서 토큰 가져오기
		String token = resolveToken(request);

		// 2. 토큰 검증 및 인증 정보 생성
		if (token != null && jwtTokenProvider.isTokenValid(token)) {
			if (redisService.hasKey(token)) {
				throw new JwtException("Already logged out User");
			}

			// 토큰으로부터 유저 정보 받아오기
			Authentication authentication = getAuthentication(token);
			// SecurityContext 에 Authentication 객체 저장
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}

		filterChain.doFilter(request, response);
	}

	// Request Header에서 token 값 추출
	public String resolveToken(HttpServletRequest request) {
		if (request.getCookies() == null)
			return null;

		for (Cookie cookie : request.getCookies()) {
			if (AppConstants.ACCESS_TOKEN_NAME_PREFIX.equals(cookie.getName())) {
				return cookie.getValue();
			}
		}
		return null; // 토큰이 없으면 null 반환
	}

	// 토큰으로 Authentication 객체 생성
	public Authentication getAuthentication(String jwt) {
		PrincipalDetails principalDetails = (PrincipalDetails)principalDetailsService.loadUserByUsername(
			jwtTokenProvider.getEmail(jwt));
		return new UsernamePasswordAuthenticationToken(principalDetails, "", principalDetails.getAuthorities());
	}
}
