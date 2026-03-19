package com.payflow.accountservice.client;

import com.payflow.accountservice.exception.UserServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class UserServiceClient {

    private final WebClient.Builder webClientBuilder;
    private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;

    @Value("${services.user-service.url}")
    private String userServiceUrl;

    public boolean userExists(Long userId) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("userService");

        return circuitBreaker.run(
                () -> Boolean.TRUE.equals(
                        webClientBuilder
                                .baseUrl(userServiceUrl)
                                .build()
                                .get()
                                .uri("/api/users/{id}/exists", userId)
                                .retrieve()
                                .bodyToMono(Boolean.class)
                                .block()
                ),
                throwable -> fallbackUserExists()
        );
    }

    private boolean fallbackUserExists() {
        throw new UserServiceUnavailableException();
    }
}