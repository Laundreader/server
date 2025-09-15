package com.laundreader.userapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.laundreader.common.error.exception.Exception401;
import com.laundreader.common.error.exception.Exception403;
import com.laundreader.userapi._core.security.FilterResponseUtils;
import com.laundreader.userapi._core.security.jwt.JwtAuthenticationFilter;
import com.laundreader.userapi._core.security.jwt.JwtExceptionFilter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@Slf4j
@RequiredArgsConstructor
public class SecurityConfig {
	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final JwtExceptionFilter jwtExceptionFilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())  // CSRF 해제
			.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin())) // iframe 허용
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 사용 거부
			.formLogin(form -> form.disable()) // form 로그인 해제
			.httpBasic(basic -> basic.disable()); // basic auth 해제

		// JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 앞에 넣는다
		http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
		http.addFilterBefore(jwtExceptionFilter, JwtAuthenticationFilter.class);

		http.exceptionHandling(auth -> {
			// 인증 실패 처리
			auth.authenticationEntryPoint((request, response, authException) -> {
				log.warn(request.getRequestURI() + " 인증되지 않은 사용자가 자원에 접근하려 합니다: " + authException.getMessage());
				FilterResponseUtils.unAuthorized(response, new Exception401("인증되지 않았습니다"));
			});

			// 권한 실패 처리
			auth.accessDeniedHandler((request, response, accessDeniedException) -> {
				log.warn(request.getRequestURI() + " 권한이 없는 사용자가 자원에 접근하려 합니다: " + accessDeniedException.getMessage());
				FilterResponseUtils.forbidden(response, new Exception403("권한이 없습니다"));
			});
		});

		// 인증, 권한 필터 설정
		http.authorizeHttpRequests(auth -> auth
			.requestMatchers("/actuator/**", "/user-api/**").permitAll()
			.anyRequest().authenticated()
		);

		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws
		Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
}