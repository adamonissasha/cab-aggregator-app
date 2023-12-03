package com.example.bankservice.webClient;

import com.example.bankservice.dto.response.CardHolderResponse;
import com.example.bankservice.dto.response.ExceptionResponse;
import com.example.bankservice.exception.PassengerNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class PassengerWebClient {
    @Value("${passenger-service.url}")
    private String passengerServiceUrl;
    private final WebClient webClient;
    public CardHolderResponse getPassenger(long id) {
        return webClient.get()
                .uri(passengerServiceUrl + "/{id}", id)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        response -> {
                            if (response.statusCode() == HttpStatus.NOT_FOUND) {
                                return response.bodyToMono(ExceptionResponse.class)
                                        .flatMap(exceptionResponse -> Mono.error(
                                                new PassengerNotFoundException(exceptionResponse.getMessage())));
                            }
                            return Mono.empty();
                        }
                )
                .bodyToMono(CardHolderResponse.class)
                .block();
    }
}
