package com.laundreader.userapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // 모든 경로
                        .allowedOriginPatterns("*") // 허용할 프론트 주소
                        .allowedMethods("*") // 모든 HTTP 메서드
                        .allowedHeaders("*")
                        .allowCredentials(true); // 쿠키 포함 가능
            }
        };
    }
}
