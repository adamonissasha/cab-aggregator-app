package com.example.passengerservice.service.impl;

import com.example.passengerservice.dto.request.PassengerRequest;
import com.example.passengerservice.dto.response.PassengerResponse;
import com.example.passengerservice.exception.PassengerNotFoundException;
import com.example.passengerservice.exception.PhoneNumberUniqueException;
import com.example.passengerservice.model.Passenger;
import com.example.passengerservice.repository.PassengerRepository;
import com.example.passengerservice.service.PassengerService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PassengerServiceImpl implements PassengerService {
    private static final String PASSENGER_NOT_FOUND = "Passenger not found!";
    private static final String PHONE_NUMBER_EXIST = "Passenger with this phone number already exist!";
    private final PassengerRepository passengerRepository;
    private final ModelMapper modelMapper;

    @Override
    public PassengerResponse createPassenger(PassengerRequest passengerRequest) {
        Passenger newPassenger = mapPassengerRequestToPassenger(passengerRequest);
        Optional<Passenger> optionalPassenger =
                passengerRepository.findPassengerByPhoneNumber(passengerRequest.getPhoneNumber());
        if (optionalPassenger.isPresent()) {
            throw new PhoneNumberUniqueException(PHONE_NUMBER_EXIST);
        } else {
            newPassenger = passengerRepository.save(newPassenger);
            return mapPassengerToPassengerResponse(newPassenger);
        }
    }

    @Override
    public PassengerResponse editPassenger(long id, PassengerRequest passengerRequest) {
        Passenger updatedPassenger = mapPassengerRequestToPassenger(passengerRequest);
        Passenger existingPassenger = passengerRepository.findById(id)
                .orElseThrow(() -> new PassengerNotFoundException(PASSENGER_NOT_FOUND));
        if (passengerRepository.findPassengerByPhoneNumberAndIdIsNot(passengerRequest.getPhoneNumber(), id).isPresent()) {
            throw new PhoneNumberUniqueException(PHONE_NUMBER_EXIST);
        }
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


    public Passenger mapPassengerRequestToPassenger(PassengerRequest passengerRequest) {
        return modelMapper.map(passengerRequest, Passenger.class);
    }

    public PassengerResponse mapPassengerToPassengerResponse(Passenger passenger) {
        return modelMapper.map(passenger, PassengerResponse.class);
    }
}
