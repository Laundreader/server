package com.laundreader.userapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")  // 모든 경로에 대해
			.allowedOrigins("https://laundreader.com", "http://localhost:5173") // 허용 도메인
			.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")         // 허용 메서드
			.allowedHeaders("Authorization", "Content-Type")                   // 허용 헤더
			.allowCredentials(true);                                           // 쿠키 허용
	}
}

