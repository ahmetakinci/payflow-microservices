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

@Component
@RequiredArgsConstructor
public class AccountServiceClient {

    private final WebClient.Builder webClientBuilder;
    private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;
    private static final String ACCOUNT_SERVICE = "account-service";

    @Value("${services.account-service.url}")
    private String accountServiceUrl;

    public BigDecimal getBalance(String accountNumber) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create(ACCOUNT_SERVICE);

        return circuitBreaker.run(
                () -> {
                    AccountResponse response = webClientBuilder
                            .baseUrl(accountServiceUrl)
                            .build()
                            .get()
                            .uri("/api/accounts/number/{accountNumber}", accountNumber)
                            .retrieve()
                            .bodyToMono(AccountResponse.class)
                            .block();
                    return response != null ? response.getBalance() : null;
                },
                throwable -> fallbackGetBalance()
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
                            .uri("/api/accounts/number/{accountNumber}/debit", accountNumber)
                            .bodyValue(amount)
                            .retrieve()
                            .toBodilessEntity()
                            .block();
                    return null;
                },
                throwable -> {
                    fallbackDebitandCredit();
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
                            .uri("/api/accounts/number/{accountNumber}/credit", accountNumber)
                            .bodyValue(amount)
                            .retrieve()
                            .toBodilessEntity()
                            .block();
                    return null;
                },
                throwable -> {
                    fallbackDebitandCredit();
                    return null;
                }
        );
    }

    private BigDecimal fallbackGetBalance() {
        throw new AccountServiceUnavailableException();
    }

    private void fallbackDebitandCredit() {
        throw new AccountServiceUnavailableException();
    }

}