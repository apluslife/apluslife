package com.apluslife.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Spring MVC 웹 설정
 * CORS, 리소스 핸들러, 인터셉터 등의 웹 관련 설정을 통합 관리
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class WebMvcConfigurer extends WebMvcConfigurationSupport {

    /**
     * CORS 설정
     * 크로스 도메인 요청에 대한 정책 정의
     */
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry
                .addMapping("/**")  // 모든 경로에 CORS 적용
                .allowedOrigins("*")  // 모든 오리진 허용 (필요시 제한)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 허용 메서드
                .allowedHeaders("*")  // 모든 헤더 허용
                .allowCredentials(false)  // 인증정보 포함 여부
                .maxAge(3600);  // CORS 캐시 시간 (초)

        log.info("CORS 설정 완료");
    }

    /**
     * 정적 리소스 핸들러 설정
     * CSS, JS, 이미지 등의 정적 리소스 경로 매핑
     */
    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // CSS, JS, 이미지 정적 리소스
        registry
                .addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/")
                .setCachePeriod(31536000);  // 1년 캐시

        registry
                .addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/")
                .setCachePeriod(31536000);

        registry
                .addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/")
                .setCachePeriod(31536000);

        registry
                .addResourceHandler("/favicon.ico")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(31536000);

        // Webjars (부트스트랩, jQuery 등)
        registry
                .addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/")
                .setCachePeriod(31536000);

        // SmartEditor 리소스
        registry
                .addResourceHandler("/smarteditor/**")
                .addResourceLocations("classpath:/static/smarteditor/")
                .setCachePeriod(31536000);

        // 파일 업로드 디렉토리 (운영 환경)
        // 필요시 외부 디렉토리 경로로 변경
        registry
                .addResourceHandler("/uploads/**")
                .addResourceLocations("file:C:/apluslife/uploads/");

        log.info("정적 리소스 핸들러 설정 완료");
    }

    /**
     * 인터셉터 설정
     * 요청/응답에 대한 사전/사후 처리 설정
     */
    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        // 향후 필요시 인터셉터 추가 예정

        log.info("인터셉터 설정 완료");
    }

    /**
     * 문자 인코딩 설정
     * 요청/응답 문자 인코딩을 UTF-8로 통일
     * (application.yml의 server.servlet.encoding 설정과 함께 작동)
     */
    // Spring Boot 3.x에서는 application.yml 설정으로 충분
    // 필요시 여기에 추가 설정 가능
}
