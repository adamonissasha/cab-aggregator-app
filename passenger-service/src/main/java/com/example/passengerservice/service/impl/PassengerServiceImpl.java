package com.example.passengerservice.service.impl;

import com.example.passengerservice.dto.request.PassengerRequest;
import com.example.passengerservice.dto.response.PassengerPageResponse;
import com.example.passengerservice.dto.response.PassengerResponse;
import com.example.passengerservice.exception.PassengerNotFoundException;
import com.example.passengerservice.exception.PhoneNumberUniqueException;
import com.example.passengerservice.model.Passenger;
import com.example.passengerservice.repository.PassengerRepository;
import com.example.passengerservice.service.PassengerRatingService;
import com.example.passengerservice.service.PassengerService;
import com.example.passengerservice.util.FieldValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class PassengerServiceImpl implements PassengerService {
    private static final String PASSENGER_NOT_FOUND = "Passenger with id '%s' not found";
    private static final String PHONE_NUMBER_EXIST = "Passenger with phone number '%s' already exist";
    private final PassengerRepository passengerRepository;
    private final ModelMapper modelMapper;
    private final PassengerRatingService passengerRatingService;
    private final FieldValidator fieldValidator;

    @Override
    public Mono<PassengerResponse> createPassenger(PassengerRequest passengerRequest) {
        log.info("Creating passenger: {}", passengerRequest);
        String phoneNumber = passengerRequest.getPhoneNumber();
        return passengerRepository.findPassengerByPhoneNumber(phoneNumber)
                .flatMap(passenger -> {
                    log.error("Passenger with phone number {} already exist", phoneNumber);
                    return Mono.error(new PhoneNumberUniqueException(String.format(PHONE_NUMBER_EXIST, phoneNumber)));
                })
                .hasElement()
                .flatMap(passengerExists -> mapPassengerRequestToPassenger(passengerRequest)
                        .flatMap(passengerRepository::save)
                        .flatMap(this::mapPassengerToPassengerResponse));
    }

    @Override
    public Mono<PassengerResponse> editPassenger(String id, PassengerRequest passengerRequest) {
        log.info("Updating passenger with id {}: {}", id, passengerRequest);

        return getPassenger(id)
                .flatMap(existingPassenger -> {
                    String newPhoneNumber = passengerRequest.getPhoneNumber();
                    if (!existingPassenger.getPhoneNumber().equals(newPhoneNumber)) {
                        return passengerRepository.findPassengerByPhoneNumber(newPhoneNumber)
                                .flatMap(passenger -> {
                                    log.error("Passenger with phone number {} already exists", newPhoneNumber);
                                    return Mono.error(new PhoneNumberUniqueException(String.format(PHONE_NUMBER_EXIST, newPhoneNumber)));
                                })
                                .switchIfEmpty(Mono.just(existingPassenger));
                    } else {
                        return Mono.just(existingPassenger);
                    }
                })
                .flatMap(existingPassenger -> mapPassengerRequestToPassenger(passengerRequest))
                .flatMap(updatedPassenger -> {
                    updatedPassenger.setId(id);
                    return passengerRepository.save(updatedPassenger);
                })
                .flatMap(this::mapPassengerToPassengerResponse);
    }

    @Override
    public Mono<PassengerResponse> getPassengerById(String id) {
        log.info("Retrieving passenger by id: {}", id);

        return getPassenger(id)
                .flatMap(this::mapPassengerToPassengerResponse);
    }

    @Override
    public Mono<PassengerPageResponse> getAllPassengers(int page, int size, String sortBy) {
        log.info("Retrieving all passengers with pagination: page={}, size={}, sortBy={}", page, size, sortBy);

        return fieldValidator.checkSortField(Passenger.class, sortBy)
                .then(Mono.defer(() -> {
                    Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
                    return passengerRepository.findAllByIsActiveTrue(pageable)
                            .flatMap(this::mapPassengerToPassengerResponse)
                            .collectList()
                            .flatMap(passengerResponses -> passengerRepository.countAllByIsActiveTrue()
                                    .map(totalElements -> PassengerPageResponse.builder()
                                            .passengers(passengerResponses)
                                            .totalPages((int) Math.ceil((double) totalElements / size))
                                            .totalElements(totalElements)
                                            .currentPage(page)
                                            .pageSize(size)
                                            .build()
                                    )
                            );
                }));
    }

    @Override
    @Transactional
    public Mono<Void> deletePassengerById(String id) {
        log.info("Deleting passenger by id: {}", id);

        return getPassenger(id)
                .flatMap(passenger -> {
                    passenger.setActive(false);
                    return passengerRepository.save(passenger)
                            .then();
                });
    }

    private Mono<Passenger> mapPassengerRequestToPassenger(PassengerRequest passengerRequest) {
        Passenger passenger = modelMapper.map(passengerRequest, Passenger.class);
        passenger.setActive(true);
        return Mono.just(passenger);
    }

    private Mono<PassengerResponse> mapPassengerToPassengerResponse(Passenger passenger) {
        return passengerRatingService.getAveragePassengerRating(passenger.getId())
                .map(averageRatingResponse -> {
                    PassengerResponse passengerResponse = modelMapper.map(passenger, PassengerResponse.class);
                    passengerResponse.setRating(averageRatingResponse.getAverageRating());
                    return passengerResponse;
                });
    }

    private Mono<Passenger> getPassenger(String passengerId) {
        return passengerRepository.findById(passengerId)
                .filter(Passenger::isActive)
                .switchIfEmpty(Mono.error(() -> {
                    log.error("Passenger with id {} not found", passengerId);
                    return new PassengerNotFoundException(String.format(PASSENGER_NOT_FOUND, passengerId));
                }));
    }
}