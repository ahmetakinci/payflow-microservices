package com.payflow.accountservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${spring.security.user.name:admin}")
    private String serviceUser;

    @Value("${spring.security.user.password:admin}")
    private String servicePassword;

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .defaultHeaders(headers -> headers.setBasicAuth(serviceUser, servicePassword));
    }
}
