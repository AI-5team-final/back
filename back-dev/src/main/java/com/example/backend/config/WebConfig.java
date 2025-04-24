package com.example.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // 모든 경로에 대해
                .allowedOriginPatterns("http://localhost:3000")  // React 주소
                .allowedMethods("*")  // GET, POST 등 모두 허용
                .allowedHeaders("*")    // Authorization 헤더 포함해서 모든 요청 헤더 허용
                .allowCredentials(true);    // 쿠키, 인증 관련 헤더 포함 허용 (Authorization 포함
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/files/**") // 브라우저 요청 경로
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/uploads/"); // 실제 저장 위치
    }
}