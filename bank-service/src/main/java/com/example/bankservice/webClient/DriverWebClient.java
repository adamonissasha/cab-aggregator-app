package com.example.bankservice.webClient;

import com.example.bankservice.dto.response.CardHolderResponse;
import com.example.bankservice.dto.response.ExceptionResponse;
import com.example.bankservice.exception.DriverNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class DriverWebClient {
    @Value("${driver-service.url}")
    private String rideServiceUrl;
    private final WebClient webClient;
    public CardHolderResponse getDriver(long id) {
        return webClient.get()
                .uri(rideServiceUrl + "/{id}", id)
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
                .bodyToMono(CardHolderResponse.class)
                .block();
    }
}
