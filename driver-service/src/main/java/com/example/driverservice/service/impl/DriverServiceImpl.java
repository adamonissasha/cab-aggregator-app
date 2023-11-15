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
    private final DriverRepository driverRepository;
    private final CarRepository carRepository;
    private final ModelMapper modelMapper;

    @Override
    public DriverResponse createDriver(DriverRequest driverRequest) {
        carRepository.findById(driverRequest.getCarId())
                .orElseThrow(() -> new CarNotFoundException(CAR_NOT_FOUND));
        driverRepository.findDriverByPhoneNumber(driverRequest.getPhoneNumber())
                .orElseThrow(() -> new PhoneNumberUniqueException(PHONE_NUMBER_EXIST));
        Driver newDriver = mapDriverRequestToDriver(driverRequest);
        newDriver = driverRepository.save(newDriver);
        return mapDriverToDriverResponse(newDriver);
    }

    @Override
    public DriverResponse editDriver(long id, DriverRequest driverRequest) {
        carRepository.findById(driverRequest.getCarId())
                .orElseThrow(() -> new CarNotFoundException(CAR_NOT_FOUND));
        driverRepository.findDriverByPhoneNumberAndIdIsNot(driverRequest.getPhoneNumber(), id)
                .orElseThrow(() -> new PhoneNumberUniqueException(PHONE_NUMBER_EXIST));
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
                Arrays.asList("id", "firstName", "lastName", "phoneNumber", "email", "carId");
        if (!allowedSortFields.contains(sortBy)) {
            throw new IncorrectFieldNameException("Invalid sortBy field. Allowed fields: " + allowedSortFields);
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
