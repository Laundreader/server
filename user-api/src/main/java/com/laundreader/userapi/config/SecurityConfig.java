package com.laundreader.userapi.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.laundreader.common.error.exception.Exception401;
import com.laundreader.common.error.exception.Exception403;
import com.laundreader.userapi._core.oauth.CustomOAuth2FailureHandler;
import com.laundreader.userapi._core.oauth.CustomOAuth2SuccessHandler;
import com.laundreader.userapi._core.oauth.CustomOAuth2UserService;
import com.laundreader.userapi._core.security.jwt.JwtAuthenticationFilter;
import com.laundreader.userapi._core.security.jwt.JwtExceptionFilter;
import com.laundreader.userapi._core.security.jwt.response.FilterResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@Slf4j
@RequiredArgsConstructor
public class SecurityConfig {
	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final JwtExceptionFilter jwtExceptionFilter;
	private final CustomOAuth2UserService customOAuth2UserService;
	private final CustomOAuth2SuccessHandler successHandler;
	private final CustomOAuth2FailureHandler failureHandler;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.cors(cors -> cors
				.configurationSource(corsConfigurationSource())
			)
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
				FilterResponse.unAuthorized(response, new Exception401("인증되지 않았습니다"));
			});

			// 권한 실패 처리
			auth.accessDeniedHandler((request, response, accessDeniedException) -> {
				log.warn(request.getRequestURI() + " 권한이 없는 사용자가 자원에 접근하려 합니다: " + accessDeniedException.getMessage());
				FilterResponse.forbidden(response, new Exception403("권한이 없습니다"));
			});
		});

		// 인증, 권한 필터 설정
		http.authorizeHttpRequests(auth -> auth
			.requestMatchers("/error", "/favicon.ico").permitAll()
			.requestMatchers("/login/**", "/oauth2/**", "/auth/reissue").permitAll()
			.requestMatchers("/actuator/**", "/public/**").permitAll()
			.anyRequest().authenticated()
		);

		http.oauth2Login(oauth2 -> oauth2
			.userInfoEndpoint(userInfo -> userInfo
				.userService(customOAuth2UserService)
			)
			.successHandler(successHandler)
			.failureHandler(failureHandler)
		); // OAuth2 로그인 활성화

		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws
		Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of(
			"http://localhost:5173",
			"https://localhost:5173",
			"https://laundreader.com"
		));
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}