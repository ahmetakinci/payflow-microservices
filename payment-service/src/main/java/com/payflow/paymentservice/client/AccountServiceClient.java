package com.payflow.paymentservice.client;

import com.payflow.paymentservice.dto.AccountResponse;
import com.payflow.paymentservice.exception.AccountServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AccountServiceClient {

    private final WebClient.Builder webClientBuilder;
    private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;
    private static final String ACCOUNT_SERVICE = "account-service";

    @Value("${services.account-service.url}")
    private String accountServiceUrl;

    public AccountResponse getAccount(String accountNumber) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create(ACCOUNT_SERVICE);

        return circuitBreaker.run(
                () -> webClientBuilder
                        .baseUrl(accountServiceUrl)
                        .build()
                        .get()
                        .uri("/internal/accounts/number/{accountNumber}", accountNumber)
                        .retrieve()
                        .bodyToMono(AccountResponse.class)
                        .block(),
                throwable -> fallbackAccount()
        );
    }

    public List<AccountResponse> getAccountsByUser(Long userId) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create(ACCOUNT_SERVICE);

        return circuitBreaker.run(
                () -> webClientBuilder
                        .baseUrl(accountServiceUrl)
                        .build()
                        .get()
                        .uri("/internal/accounts/by-user/{userId}", userId)
                        .retrieve()
                        .bodyToFlux(AccountResponse.class)
                        .collectList()
                        .block(),
                throwable -> fallbackAccountList()
        );
    }

    public void debit(String accountNumber, BigDecimal amount) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create(ACCOUNT_SERVICE);

        circuitBreaker.run(
                () -> {
                    webClientBuilder
                            .baseUrl(accountServiceUrl)
                            .build()
                            .put()
                            .uri("/internal/accounts/number/{accountNumber}/debit", accountNumber)
                            .bodyValue(amount)
                            .retrieve()
                            .toBodilessEntity()
                            .block();
                    return null;
                },
                throwable -> {
                    fallbackVoid();
                    return null;
                }
        );
    }

    public void credit(String accountNumber, BigDecimal amount) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create(ACCOUNT_SERVICE);

        circuitBreaker.run(
                () -> {
                    webClientBuilder
                            .baseUrl(accountServiceUrl)
                            .build()
                            .put()
                            .uri("/internal/accounts/number/{accountNumber}/credit", accountNumber)
                            .bodyValue(amount)
                            .retrieve()
                            .toBodilessEntity()
                            .block();
                    return null;
                },
                throwable -> {
                    fallbackVoid();
                    return null;
                }
        );
    }

    private AccountResponse fallbackAccount() {
        throw new AccountServiceUnavailableException();
    }

    private List<AccountResponse> fallbackAccountList() {
        throw new AccountServiceUnavailableException();
    }

    private void fallbackVoid() {
        throw new AccountServiceUnavailableException();
    }
}
