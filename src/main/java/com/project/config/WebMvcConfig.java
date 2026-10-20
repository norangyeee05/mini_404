package com.project.config;

import com.project.web.AdminCheckInterceptor;
import com.project.web.LoginCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheckInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/api/**", "/admin/api/**",
                        "/", "/index", "/login", "/signup", "/find-id", "/find-password", "/reset-password",
                        "/terms", "/privacy", "/css/**", "/js/**", "/images/**", "/favicon.ico", "/error");

        registry.addInterceptor(new AdminCheckInterceptor()).addPathPatterns("/admin/**");
    }
}

