package com.example.driverservice.service.impl;

import com.example.driverservice.dto.request.DriverRequest;
import com.example.driverservice.dto.response.DriverPageResponse;
import com.example.driverservice.dto.response.DriverResponse;
import com.example.driverservice.exception.DriverNotFoundException;
import com.example.driverservice.exception.DriverStatusException;
import com.example.driverservice.exception.FreeDriverNotFoundException;
import com.example.driverservice.exception.PhoneNumberUniqueException;
import com.example.driverservice.model.Driver;
import com.example.driverservice.model.enums.Status;
import com.example.driverservice.repository.DriverRepository;
import com.example.driverservice.service.CarService;
import com.example.driverservice.service.DriverRatingService;
import com.example.driverservice.service.DriverService;
import com.example.driverservice.util.FieldValidator;
import com.example.driverservice.webClient.BankWebClient;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {
    private static final String DRIVER_NOT_FOUND = "Driver with id '%s' not found";
    private static final String FREE_DRIVER_NOT_FOUND = "Free driver not found";
    private static final String DRIVER_ALREADY_FREE = "Driver with id '%s' is already free";
    private static final String PHONE_NUMBER_EXIST = "Driver with phone number '%s' already exist";
    private final DriverRepository driverRepository;
    private final CarService carService;
    private final DriverRatingService driverRatingService;
    private final ModelMapper modelMapper;
    private final BankWebClient bankWebClient;
    private final FieldValidator fieldValidator;

    @Override
    public DriverResponse createDriver(DriverRequest driverRequest) {
        String phoneNumber = driverRequest.getPhoneNumber();
        carService.getCarById(driverRequest.getCarId());
        driverRepository.findDriverByPhoneNumber(phoneNumber)
                .ifPresent(driver -> {
                    throw new PhoneNumberUniqueException(String.format(PHONE_NUMBER_EXIST, phoneNumber));
                });
        Driver newDriver = mapDriverRequestToDriver(driverRequest);
        newDriver = driverRepository.save(newDriver);
        return mapDriverToDriverResponse(newDriver);
    }

    @Override
    public DriverResponse editDriver(long id, DriverRequest driverRequest) {
        carService.getCarById(driverRequest.getCarId());
        Driver existingDriver = driverRepository.findById(id)
                .orElseThrow(() -> new DriverNotFoundException(String.format(DRIVER_NOT_FOUND, id)));
        String phoneNumber = driverRequest.getPhoneNumber();
        driverRepository.findDriverByPhoneNumber(phoneNumber)
                .ifPresent(driver -> {
                    if (driver.getId() != id) {
                        throw new PhoneNumberUniqueException(String.format(PHONE_NUMBER_EXIST, phoneNumber));
                    }
                });
        Driver updatedDriver = mapDriverRequestToDriver(driverRequest);
        updatedDriver.setId(existingDriver.getId());
        updatedDriver = driverRepository.save(updatedDriver);
        return mapDriverToDriverResponse(updatedDriver);
    }

    @Override
    public DriverResponse getDriverById(long id) {
        return driverRepository.findById(id)
                .map(this::mapDriverToDriverResponse)
                .orElseThrow(() -> new DriverNotFoundException(String.format(DRIVER_NOT_FOUND, id)));
    }

    @Override
    public DriverPageResponse getAllDrivers(int page, int size, String sortBy) {
        fieldValidator.checkSortField(Driver.class, sortBy);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<Driver> driverPage = driverRepository.findAll(pageable);

        List<DriverResponse> driverResponses = driverPage.getContent()
                .stream()
                .map(this::mapDriverToDriverResponse)
                .toList();

        return DriverPageResponse.builder()
                .drivers(driverResponses)
                .totalPages(driverPage.getTotalPages())
                .totalElements(driverPage.getTotalElements())
                .currentPage(driverPage.getNumber())
                .pageSize(driverPage.getSize())
                .build();
    }

    @Override
    public DriverResponse getFreeDriver() {
        Driver freeDriver = driverRepository.findFirstByStatus(Status.FREE)
                .orElseThrow(() -> new FreeDriverNotFoundException(FREE_DRIVER_NOT_FOUND));
        freeDriver.setStatus(Status.BUSY);
        driverRepository.save(freeDriver);
        return mapDriverToDriverResponse(freeDriver);
    }

    @Override
    public DriverResponse changeDriverStatusToFree(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new DriverNotFoundException(String.format(DRIVER_NOT_FOUND, id)));
        if (driver.getStatus().equals(Status.FREE)) {
            throw new DriverStatusException(String.format(DRIVER_ALREADY_FREE, id));
        }
        driver.setStatus(Status.FREE);
        driver = driverRepository.save(driver);
        return mapDriverToDriverResponse(driver);
    }

    @Override
    public void deleteDriverById(long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new DriverNotFoundException(String.format(DRIVER_NOT_FOUND, id)));
        driver.setActive(false);
        driverRepository.save(driver);

        Long driverId = driver.getId();
        bankWebClient.deleteDriverBankAccount(driverId);
        bankWebClient.deleteDriverBankCards(driverId);
    }

    public Driver mapDriverRequestToDriver(DriverRequest driverRequest) {
        Driver driver = modelMapper.map(driverRequest, Driver.class);
        driver.setId(null);
        driver.setStatus(Status.FREE);
        driver.setActive(true);
        return driver;
    }

    public DriverResponse mapDriverToDriverResponse(Driver driver) {
        DriverResponse driverResponse = modelMapper.map(driver, DriverResponse.class);
        driverResponse.setRating(driverRatingService
                .getAverageDriverRating(driver.getId())
                .getAverageRating());
        driverResponse.setCar(carService.getCarById(driver.getCar().getId()));
        return driverResponse;
    }
}
