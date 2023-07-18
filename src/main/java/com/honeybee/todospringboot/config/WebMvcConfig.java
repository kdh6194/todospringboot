package com.honeybee.todospringboot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    // CORS요청의 최대 지속 시간을 나타냄
    private final long MAX_AGE_SECS = 3600;
    @Override
    public void addCorsMappings(CorsRegistry registry){
        //모든 경로에 CORS설정을 추가 -> 이제 함부로 접근하기 어려움
        registry.addMapping("/**")
                // localhost:3000의 요청을 허용한다 -> 해당 도메인만 접근 가능
                .allowedOrigins("http://localhost:3000")
                // 허용할 http메서드 지정
                .allowedMethods("GET","POST","PUT","DELETE","OPTIONS")
                // 허용할 요청 헤더를 지정
                .allowedHeaders("*")
                // 클라이언트 요청시 자격 증명(cookie,HTTP인증)을 포함할 수 있도록 허용
                .allowCredentials(true)
                .maxAge(MAX_AGE_SECS);
    }
}
