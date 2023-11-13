package com.example.driverservice.service.impl;

import com.example.driverservice.dto.request.DriverRequest;
import com.example.driverservice.dto.response.DriverResponse;
import com.example.driverservice.exception.CarNotFoundException;
import com.example.driverservice.exception.DriverNotFoundException;
import com.example.driverservice.exception.PhoneNumberUniqueException;
import com.example.driverservice.model.Driver;
import com.example.driverservice.repository.CarRepository;
import com.example.driverservice.repository.DriverRepository;
import com.example.driverservice.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

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
        return carRepository.findById(driverRequest.getCarId())
                .map(car -> {
                    Driver newDriver = mapDriverRequestToDriver(driverRequest);
                    if (driverRepository.findDriverByPhoneNumber(driverRequest.getPhoneNumber()).isPresent()) {
                        throw new PhoneNumberUniqueException(PHONE_NUMBER_EXIST);
                    }
                    newDriver = driverRepository.save(newDriver);
                    return mapDriverToDriverResponse(newDriver);
                })
                .orElseThrow(() -> new CarNotFoundException(CAR_NOT_FOUND));
    }

    @Override
    public DriverResponse editPassenger(long id, DriverRequest driverRequest) {
        return carRepository.findById(driverRequest.getCarId())
                .map(car -> {
                    Driver updatedDriver = mapDriverRequestToDriver(driverRequest);
                    Driver existingDriver = driverRepository.findById(id)
                            .orElseThrow(() -> new DriverNotFoundException(DRIVER_NOT_FOUND));
                    if (driverRepository.findDriverByPhoneNumberAndIdIsNot(driverRequest.getPhoneNumber(), id).isPresent()) {
                        throw new PhoneNumberUniqueException(PHONE_NUMBER_EXIST);
                    }
                    updatedDriver.setId(existingDriver.getId());
                    updatedDriver = driverRepository.save(updatedDriver);
                    return mapDriverToDriverResponse(updatedDriver);
                })
                .orElseThrow(() -> new CarNotFoundException(CAR_NOT_FOUND));
    }

    @Override
    public DriverResponse getDriverById(long id) {
        return driverRepository.findById(id)
                .map(this::mapDriverToDriverResponse)
                .orElseThrow(() -> new DriverNotFoundException(DRIVER_NOT_FOUND));
    }


    public Driver mapDriverRequestToDriver(DriverRequest driverRequest) {
        return modelMapper.map(driverRequest, Driver.class);
    }

    public DriverResponse mapDriverToDriverResponse(Driver driver) {
        return modelMapper.map(driver, DriverResponse.class);
    }
}
