package com.payflow.accountservice.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class UserServiceClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${services.user-service.url}")
    private String userServiceUrl;

    public boolean userExists(Long userId) {
        return Boolean.TRUE.equals(
                webClientBuilder
                        .baseUrl(userServiceUrl)
                        .build()
                        .get()
                        .uri("/api/users/{id}/exists", userId)
                        .retrieve()
                        .bodyToMono(Boolean.class)
                        .block()
        );
    }
}