package com.example.driverservice.service.impl;

import com.example.driverservice.dto.request.DriverRequest;
import com.example.driverservice.dto.response.DriverResponse;
import com.example.driverservice.exception.CarNotFoundException;
import com.example.driverservice.exception.DriverNotFoundException;
import com.example.driverservice.exception.IncorrectFieldNameException;
import com.example.driverservice.exception.PhoneNumberUniqueException;
import com.example.driverservice.model.Driver;
import com.example.driverservice.repository.CarRepository;
import com.example.driverservice.repository.DriverRepository;
import com.example.driverservice.service.DriverService;
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
public class DriverServiceImpl implements DriverService {
    private static final String DRIVER_NOT_FOUND = "Driver not found!";
    private static final String CAR_NOT_FOUND = "Car with this id not found!";
    private static final String PHONE_NUMBER_EXIST = "Driver with this phone number already exist!";
    private static final String INCORRECT_FIELDS = "Invalid sortBy field. Allowed fields: ";
    private static final String FIELD_ID = "id";
    private static final String FIELD_FIRST_NAME = "firstName";
    private static final String FIELD_LAST_NAME = "lastName";
    private static final String FIELD_PHONE_NUMBER = "phoneNumber";
    private static final String FIELD_EMAIL = "email";
    private static final String FIELD_CAR_ID = "carId";
    private final DriverRepository driverRepository;
    private final CarRepository carRepository;
    private final ModelMapper modelMapper;

    @Override
    public DriverResponse createDriver(DriverRequest driverRequest) {
        carRepository.findById(driverRequest.getCarId())
                .orElseThrow(() -> new CarNotFoundException(CAR_NOT_FOUND));
        driverRepository.findDriverByPhoneNumber(driverRequest.getPhoneNumber())
                .ifPresent(driver -> {
                    throw new PhoneNumberUniqueException(PHONE_NUMBER_EXIST);
                });
        Driver newDriver = mapDriverRequestToDriver(driverRequest);
        newDriver = driverRepository.save(newDriver);
        return mapDriverToDriverResponse(newDriver);
    }

    @Override
    public DriverResponse editDriver(long id, DriverRequest driverRequest) {
        carRepository.findById(driverRequest.getCarId())
                .orElseThrow(() -> new CarNotFoundException(CAR_NOT_FOUND));
        driverRepository.findDriverByPhoneNumber(driverRequest.getPhoneNumber())
                .ifPresent(driver -> {
                    throw new PhoneNumberUniqueException(PHONE_NUMBER_EXIST);
                });
        Driver existingDriver = driverRepository.findById(id)
                .orElseThrow(() -> new DriverNotFoundException(DRIVER_NOT_FOUND));
        Driver updatedDriver = mapDriverRequestToDriver(driverRequest);
        updatedDriver.setId(existingDriver.getId());
        updatedDriver = driverRepository.save(updatedDriver);
        return mapDriverToDriverResponse(updatedDriver);
    }

    @Override
    public DriverResponse getDriverById(long id) {
        return driverRepository.findById(id)
                .map(this::mapDriverToDriverResponse)
                .orElseThrow(() -> new DriverNotFoundException(DRIVER_NOT_FOUND));
    }

    @Override
    public Page<DriverResponse> getAllDrivers(int page, int size, String sortBy) {
        List<String> allowedSortFields =
                Arrays.asList(FIELD_ID, FIELD_FIRST_NAME, FIELD_LAST_NAME, FIELD_PHONE_NUMBER, FIELD_EMAIL, FIELD_CAR_ID);
        if (!allowedSortFields.contains(sortBy)) {
            throw new IncorrectFieldNameException(INCORRECT_FIELDS + allowedSortFields);
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return driverRepository.findAll(pageable)
                .map(this::mapDriverToDriverResponse);
    }


    public Driver mapDriverRequestToDriver(DriverRequest driverRequest) {
        return modelMapper.map(driverRequest, Driver.class);
    }

    public DriverResponse mapDriverToDriverResponse(Driver driver) {
        return modelMapper.map(driver, DriverResponse.class);
    }
}
