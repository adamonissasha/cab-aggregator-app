package com.example.ridesservice.webClient;

import com.example.ridesservice.dto.response.CarResponse;
import com.example.ridesservice.dto.response.DriverResponse;
import com.example.ridesservice.dto.response.ExceptionResponse;
import com.example.ridesservice.exception.driver.CarNotFoundException;
import com.example.ridesservice.exception.driver.DriverNotFoundException;
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

    public DriverResponse getFreeDriver() {
        return webClient.get()
                .uri(rideServiceUrl + "/free")
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
                .bodyToMono(DriverResponse.class)
                .block();
    }

    public DriverResponse getDriver(long id) {
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
                .bodyToMono(DriverResponse.class)
                .block();
    }

    public CarResponse getCar(long id) {
        return webClient.get()
                .uri(rideServiceUrl + "/car/{id}", id)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        response -> {
                            if (response.statusCode() == HttpStatus.NOT_FOUND) {
                                return response.bodyToMono(ExceptionResponse.class)
                                        .flatMap(exceptionResponse -> Mono.error(
                                                new CarNotFoundException(exceptionResponse.getMessage())));
                            }
                            return Mono.empty();
                        }
                )
                .bodyToMono(CarResponse.class)
                .block();
    }

    public void changeDriverStatusToFree(Long driverId) {
        webClient.get()
                .uri(rideServiceUrl + "/{driverId}/free", driverId)
                .retrieve()
                .bodyToMono(DriverResponse.class)
                .subscribe();
    }
}
