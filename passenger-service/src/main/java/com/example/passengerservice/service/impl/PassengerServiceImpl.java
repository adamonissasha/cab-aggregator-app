package com.example.passengerservice.service.impl;

import com.example.passengerservice.dto.request.PassengerRequest;
import com.example.passengerservice.dto.response.PassengerPageResponse;
import com.example.passengerservice.dto.response.PassengerResponse;
import com.example.passengerservice.exception.IncorrectFieldNameException;
import com.example.passengerservice.exception.PassengerNotFoundException;
import com.example.passengerservice.exception.PhoneNumberUniqueException;
import com.example.passengerservice.model.Passenger;
import com.example.passengerservice.repository.PassengerRepository;
import com.example.passengerservice.service.PassengerRatingService;
import com.example.passengerservice.service.PassengerService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PassengerServiceImpl implements PassengerService {
    private static final String PASSENGER_NOT_FOUND = "Passenger with id '%s' not found";
    private static final String PHONE_NUMBER_EXIST = "Passenger with phone number '%s' already exist";
    private static final String INCORRECT_FIELDS = "Invalid sortBy field. Allowed fields: ";
    private final PassengerRepository passengerRepository;
    private final ModelMapper modelMapper;
    private final PassengerRatingService passengerRatingService;

    @Override
    public PassengerResponse createPassenger(PassengerRequest passengerRequest) {
        String phoneNumber = passengerRequest.getPhoneNumber();
        passengerRepository.findPassengerByPhoneNumber(phoneNumber)
                .ifPresent(passenger -> {
                    throw new PhoneNumberUniqueException(String.format(PHONE_NUMBER_EXIST, phoneNumber));
                });
        Passenger newPassenger = mapPassengerRequestToPassenger(passengerRequest);
        newPassenger = passengerRepository.save(newPassenger);
        return mapPassengerToPassengerResponse(newPassenger);
    }

    @Override
    public PassengerResponse editPassenger(long id, PassengerRequest passengerRequest) {
        Passenger existingPassenger = passengerRepository.findById(id)
                .orElseThrow(() -> new PassengerNotFoundException(String.format(PASSENGER_NOT_FOUND, id)));
        String phoneNumber = passengerRequest.getPhoneNumber();
        passengerRepository.findPassengerByPhoneNumber(phoneNumber)
                .ifPresent(passenger -> {
                    if (passenger.getId() != id) {
                        throw new PhoneNumberUniqueException(String.format(PHONE_NUMBER_EXIST, phoneNumber));
                    }
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
                .orElseThrow(() -> new PassengerNotFoundException(String.format(PASSENGER_NOT_FOUND, id)));
    }

    @Override
    public PassengerPageResponse getAllPassengers(int page, int size, String sortBy) {
        checkSortField(sortBy);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<Passenger> passengerPage = passengerRepository.findAll(pageable);
        List<PassengerResponse> passengerResponses = passengerPage.getContent()
                .stream()
                .map(this::mapPassengerToPassengerResponse)
                .toList();

        return PassengerPageResponse.builder()
                .passengers(passengerResponses)
                .totalPages(passengerPage.getTotalPages())
                .totalElements(passengerPage.getTotalElements())
                .currentPage(passengerPage.getNumber())
                .pageSize(passengerPage.getSize())
                .build();
    }


    public void checkSortField(String sortBy) {
        List<String> allowedSortFields = new ArrayList<>();
        getFieldNamesRecursive(Passenger.class, allowedSortFields);
        if (!allowedSortFields.contains(sortBy)) {
            throw new IncorrectFieldNameException(INCORRECT_FIELDS + allowedSortFields);
        }
    }

    private static void getFieldNamesRecursive(Class<?> myClass, List<String> fieldNames) {
        if (myClass != null) {
            Field[] fields = myClass.getDeclaredFields();
            for (Field field : fields) {
                fieldNames.add(field.getName());
            }
            getFieldNamesRecursive(myClass.getSuperclass(), fieldNames);
        }
    }

    public Passenger mapPassengerRequestToPassenger(PassengerRequest passengerRequest) {
        return modelMapper.map(passengerRequest, Passenger.class);
    }

    public PassengerResponse mapPassengerToPassengerResponse(Passenger passenger) {
        PassengerResponse passengerResponse = modelMapper.map(passenger, PassengerResponse.class);
        passengerResponse.setRating(passengerRatingService.getAveragePassengerRating(passenger.getId())
                .getAverageRating());
        return passengerResponse;
    }
}
