package com.gym.crm.config;

import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan(
        basePackages = "com.gym.crm",
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = WebConfig.class),
                @ComponentScan.Filter(type = FilterType.ANNOTATION, value = RestController.class)
        }
)
@PropertySource("classpath:application.properties")
public class AppConfig {

    @Bean
    public jakarta.validation.Validator validator() {
        LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean();
        factoryBean.setMessageInterpolator(new ParameterMessageInterpolator());
        factoryBean.afterPropertiesSet();
        return factoryBean.getValidator();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

