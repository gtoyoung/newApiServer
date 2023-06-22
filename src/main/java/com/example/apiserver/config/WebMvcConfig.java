package com.example.apiserver.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.*;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

//    private final JwtTokenInterceptor jwtTokenInterceptor;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new PageableHandlerMethodArgumentResolver());
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 추후 예외 경로 추가
//        registry.addInterceptor(jwtTokenInterceptor).addPathPatterns("/**").excludePathPatterns("/signUp");
    }
}