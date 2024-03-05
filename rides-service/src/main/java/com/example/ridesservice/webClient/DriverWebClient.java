package com.example.ridesservice.webClient;

import com.example.ridesservice.dto.response.CarResponse;
import com.example.ridesservice.dto.response.DriverResponse;
import com.example.ridesservice.dto.response.ExceptionResponse;
import com.example.ridesservice.exception.driver.CarNotFoundException;
import com.example.ridesservice.exception.driver.DriverNotFoundException;
import com.example.ridesservice.util.FallbackResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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
    public DriverResponse getDriver(long id) {
        return webClient.get()
                .uri(driverServiceUrl + "/{id}", id)
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

    @CircuitBreaker(name = "driver-service", fallbackMethod = "getCarFallback")
    public CarResponse getCar(long id) {
        return webClient.get()
                .uri(driverServiceUrl + "/car/{id}", id)
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
        webClient.put()
                .uri(driverServiceUrl + "/{driverId}/free", driverId)
                .retrieve()
                .bodyToMono(DriverResponse.class)
                .subscribe();
    }

    private DriverResponse getDriverFallback(Throwable throwable) {
        log.error("Driver service is unavailable. Fallback method called.");
        return FallbackResponse.getDriverFallbackResponse();
    }

    private CarResponse getCarFallback(Throwable throwable) {
        log.error("Driver service is unavailable. Fallback method called.");
        return FallbackResponse.getCarFallbackResponse();
    }
}
