package com.example.passengerservice.service.impl;

import com.example.passengerservice.dto.request.PassengerRequest;
import com.example.passengerservice.dto.response.PassengerResponse;
import com.example.passengerservice.exception.IncorrectFieldNameException;
import com.example.passengerservice.exception.PassengerNotFoundException;
import com.example.passengerservice.exception.PhoneNumberUniqueException;
import com.example.passengerservice.model.Passenger;
import com.example.passengerservice.repository.PassengerRepository;
import com.example.passengerservice.service.PassengerService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PassengerServiceImpl implements PassengerService {
    private static final String PASSENGER_NOT_FOUND = "Passenger not found!";
    private static final String PHONE_NUMBER_EXIST = "Passenger with this phone number already exist!";
    private static final String INCORRECT_FIELDS = "Invalid sortBy field. Allowed fields: ";
    private static final String FIELD_ID = "id";
    private static final String FIELD_FIRST_NAME = "firstName";
    private static final String FIELD_LAST_NAME = "lastName";
    private static final String FIELD_PHONE_NUMBER = "phoneNumber";
    private static final String FIELD_EMAIL = "email";
    private final PassengerRepository passengerRepository;
    private final ModelMapper modelMapper;

    @Override
    public PassengerResponse createPassenger(PassengerRequest passengerRequest) {
        passengerRepository.findPassengerByPhoneNumber(passengerRequest.getPhoneNumber())
                .ifPresent(passenger -> {
                    throw new PhoneNumberUniqueException(PHONE_NUMBER_EXIST);
                });
        Passenger newPassenger = mapPassengerRequestToPassenger(passengerRequest);
        newPassenger = passengerRepository.save(newPassenger);
        return mapPassengerToPassengerResponse(newPassenger);
    }

    @Override
    public PassengerResponse editPassenger(long id, PassengerRequest passengerRequest) {
        Passenger existingPassenger = passengerRepository.findById(id)
                .orElseThrow(() -> new PassengerNotFoundException(PASSENGER_NOT_FOUND));
        passengerRepository.findPassengerByPhoneNumber(passengerRequest.getPhoneNumber())
                .ifPresent(passenger -> {
                    throw new PhoneNumberUniqueException(PHONE_NUMBER_EXIST);
                });
        Passenger updatedPassenger = mapPassengerRequestToPassenger(passengerRequest);
        updatedPassenger.setId(existingPassenger.getId());
        updatedPassenger = passengerRepository.save(updatedPassenger);
        return mapPassengerToPassengerResponse(updatedPassenger);
    }

    @Override
    public PassengerResponse getPassengerById(long id) {
        return passengerRepository.findById(id)
                .map(this::mapPassengerToPassengerResponse)
                .orElseThrow(() -> new PassengerNotFoundException(PASSENGER_NOT_FOUND));
    }

    @Override
    public Page<PassengerResponse> getAllPassengers(int page, int size, String sortBy) {
        List<String> allowedSortFields =
                Arrays.asList(FIELD_ID, FIELD_FIRST_NAME, FIELD_LAST_NAME, FIELD_PHONE_NUMBER, FIELD_EMAIL);
        if (!allowedSortFields.contains(sortBy)) {
            throw new IncorrectFieldNameException(INCORRECT_FIELDS + allowedSortFields);
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return passengerRepository.findAll(pageable)
                .map(this::mapPassengerToPassengerResponse);
    }


    public Passenger mapPassengerRequestToPassenger(PassengerRequest passengerRequest) {
        return modelMapper.map(passengerRequest, Passenger.class);
    }

    public PassengerResponse mapPassengerToPassengerResponse(Passenger passenger) {
        return modelMapper.map(passenger, PassengerResponse.class);
    }
}
