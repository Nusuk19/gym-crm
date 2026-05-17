package com.gym.crm.config;

import com.gym.crm.security.SecurityContextClearingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@ComponentScan("com.gym.crm.controller")
public class WebConfig implements WebMvcConfigurer {

    private SecurityContextClearingInterceptor clearingInterceptor;

    @Autowired
    public void setClearingInterceptor(SecurityContextClearingInterceptor clearingInterceptor) {
        this.clearingInterceptor = clearingInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(clearingInterceptor);
    }
}