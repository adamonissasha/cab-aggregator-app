package com.example.passengerservice.webClient;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class BankWebClient {
    @Value("${bank-service.url}")
    private String bankServiceUrl;
    private final WebClient webClient;

    public Mono<Void> deletePassengerBankCards(String passengerId) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(bankServiceUrl + "/card/user/{passengerId}");
        builder.queryParam("bankUser", "PASSENGER");
        URI uri = builder.buildAndExpand(passengerId).toUri();

        return webClient.delete()
                .uri(uri)
                .retrieve()
                .toBodilessEntity()
                .then();
    }
}
