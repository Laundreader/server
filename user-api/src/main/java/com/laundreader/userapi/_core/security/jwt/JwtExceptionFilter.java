package com.laundreader.userapi._core.security.jwt;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.laundreader.common.error.exception.Exception400;
import com.laundreader.userapi._core.security.jwt.response.FilterResponse;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*
 * 인증 오류가 아닌, JWT 관련 오류만 핸들링 하는 필터.
 * */
@Component
public class JwtExceptionFilter extends OncePerRequestFilter {
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		try {
			filterChain.doFilter(request, response);
		} catch (JwtException ex) {
			setErrorResponse(request, response, ex);
		}
	}

	public void setErrorResponse(HttpServletRequest req, HttpServletResponse res, Throwable ex) throws IOException {
		FilterResponse.badRequest(res, new Exception400("JWT", ex.getMessage()));
	}
}
