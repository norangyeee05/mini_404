package com._404._404.config;

import com._404._404.web.AdminCheckInterceptor;
import com._404._404.web.LoginCheckInterceptor;
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
						"/login", "/signup", "/find-id", "/find-password", "/reset-password",
						"/terms", "/privacy",
						"/css/**", "/js/**", "/images/**", "/favicon.ico", "/error");

		registry.addInterceptor(new AdminCheckInterceptor())
				.addPathPatterns("/admin/**");
	}
}
