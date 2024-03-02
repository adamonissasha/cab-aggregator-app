package com.example.driverservice.service.impl;

import com.example.driverservice.dto.request.DriverRequest;
import com.example.driverservice.dto.response.DriverPageResponse;
import com.example.driverservice.dto.response.DriverResponse;
import com.example.driverservice.exception.DriverNotFoundException;
import com.example.driverservice.exception.DriverStatusException;
import com.example.driverservice.exception.PhoneNumberUniqueException;
import com.example.driverservice.kafka.service.KafkaFreeDriverService;
import com.example.driverservice.model.Driver;
import com.example.driverservice.model.enums.Status;
import com.example.driverservice.repository.DriverRepository;
import com.example.driverservice.service.CarService;
import com.example.driverservice.service.DriverRatingService;
import com.example.driverservice.service.DriverService;
import com.example.driverservice.util.FieldValidator;
import com.example.driverservice.webClient.BankWebClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DriverServiceImpl implements DriverService {
    private static final String DRIVER_NOT_FOUND = "Driver with id '%s' not found";
    private static final String DRIVER_ALREADY_FREE = "Driver with id '%s' is already free";
    private static final String PHONE_NUMBER_EXIST = "Driver with phone number '%s' already exist";
    private final DriverRepository driverRepository;
    private final CarService carService;
    private final DriverRatingService driverRatingService;
    private final ModelMapper modelMapper;
    private final BankWebClient bankWebClient;
    private final FieldValidator fieldValidator;
    private final KafkaFreeDriverService kafkaFreeDriverService;

    @Override
    public DriverResponse createDriver(DriverRequest driverRequest) {
        log.info("Creating driver: {}", driverRequest);

        carService.getCarById(driverRequest.getCarId());

        String phoneNumber = driverRequest.getPhoneNumber();
        driverRepository.findDriverByPhoneNumber(phoneNumber)
                .ifPresent(driver -> {
                    throw new PhoneNumberUniqueException(String.format(PHONE_NUMBER_EXIST, phoneNumber));
                });

        Driver newDriver = mapDriverRequestToDriver(driverRequest);
        newDriver = driverRepository.save(newDriver);
        DriverResponse driverResponse = mapDriverToDriverResponse(newDriver);

        kafkaFreeDriverService.sendFreeDriverToConsumer(driverResponse);

        return driverResponse;
    }

    @Override
    public DriverResponse editDriver(long id, DriverRequest driverRequest) {
        log.info("Updating driver with id {}: {}", id, driverRequest);

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
        log.info("Retrieving driver by id: {}", id);

        return driverRepository.findById(id)
                .map(this::mapDriverToDriverResponse)
                .orElseThrow(() -> new DriverNotFoundException(String.format(DRIVER_NOT_FOUND, id)));
    }

    @Override
    public DriverPageResponse getAllDrivers(int page, int size, String sortBy) {
        log.info("Retrieving all drivers with pagination: page={}, size={}, sortBy={}", page, size, sortBy);

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
    public DriverResponse changeDriverStatusToFree(Long id) {
        log.info("Changing status of driver with id {} to FREE", id);

        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new DriverNotFoundException(String.format(DRIVER_NOT_FOUND, id)));

        if (driver.getStatus() == Status.FREE) {
            throw new DriverStatusException(String.format(DRIVER_ALREADY_FREE, id));
        }

        driver.setStatus(Status.FREE);
        driver = driverRepository.save(driver);
        DriverResponse driverResponse = mapDriverToDriverResponse(driver);

        kafkaFreeDriverService.sendFreeDriverToConsumer(driverResponse);

        return driverResponse;
    }

    @Override
    @Transactional
    public void deleteDriverById(long id) {
        log.info("Deleting driver with id: {}", id);

        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new DriverNotFoundException(String.format(DRIVER_NOT_FOUND, id)));

        driver.setActive(false);
        driverRepository.save(driver);

        bankWebClient.deleteDriverBankAccount(id);
        bankWebClient.deleteDriverBankCards(id);
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
