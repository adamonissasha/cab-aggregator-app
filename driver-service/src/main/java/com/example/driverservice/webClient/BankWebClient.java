package com.example.driverservice.webClient;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class BankWebClient {
    @Value("${bank-service.url}")
    private String bankServiceUrl;
    private final WebClient webClient;

    public void deleteDriverBankAccount(Long driverId) {
        webClient.delete()
                .uri(bankServiceUrl + "/account/{driverId}", driverId)
                .retrieve()
                .toBodilessEntity()
                .subscribe();
    }

    public void deleteDriverBankCards(Long driverId) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(bankServiceUrl + "/card/user/{driverId}");
        builder.queryParam("bankUser", "DRIVER");
        URI uri = builder.buildAndExpand(driverId).toUri();

        webClient.delete()
                .uri(uri)
                .retrieve()
                .toBodilessEntity()
                .subscribe();
    }
}
