package com.example.bankservice.webClient;

import com.example.bankservice.dto.response.BankUserResponse;
import com.example.bankservice.dto.response.ExceptionResponse;
import com.example.bankservice.exception.DriverNotFoundException;
import com.example.bankservice.util.BankUserFallbackResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Setter
@Slf4j
public class DriverWebClient {
    @Value("${driver-service.url}")
    private String driverServiceUrl;
    private final WebClient webClient;

    @CircuitBreaker(name = "driver-service", fallbackMethod = "getDriverFallback")
    public BankUserResponse getDriver(long id) {
        return webClient.get()
                .uri(driverServiceUrl + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        response -> {
                            if (response.statusCode() == HttpStatus.NOT_FOUND) {
                                return response.bodyToMono(ExceptionResponse.class)
                                        .flatMap(exceptionResponse -> Mono.error(
                                                new DriverNotFoundException(exceptionResponse.getMessage())));
                            }
                            return Mono.empty();
                        }
                )
                .bodyToMono(BankUserResponse.class)
                .block();
    }

    private BankUserResponse getDriverFallback(Throwable throwable) {
        log.error("Driver service is unavailable. Fallback method called.");
        return BankUserFallbackResponse.getBankUserFallbackResponse();
    }
}
