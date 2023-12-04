package com.example.ridesservice.webClient;

import com.example.ridesservice.dto.request.RefillRequest;
import com.example.ridesservice.dto.response.ExceptionResponse;
import com.example.ridesservice.exception.bank.BankAccountNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BankWebClient {
    @Value("${bank-service.url}")
    private String bankServiceUrl;
    private final WebClient webClient;

    public void refillDriverBankAccount(RefillRequest refillRequest) {
        webClient.put()
                .uri(bankServiceUrl + "/account/refill")
                .bodyValue(refillRequest)
                .exchangeToMono(response -> {
                    if (response.statusCode().is4xxClientError()) {
                        if (response.statusCode() == HttpStatus.NOT_FOUND) {
                            return response.bodyToMono(ExceptionResponse.class)
                                    .flatMap(exceptionResponse -> Mono.error(
                                            new BankAccountNotFoundException(exceptionResponse.getMessage())));
                        }
                    }
                    return response.bodyToMono(Void.class);
                })
                .then()
                .block();
    }
}
