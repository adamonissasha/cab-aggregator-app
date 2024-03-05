package com.example.driverservice.service.impl;

import com.example.driverservice.dto.request.CarRequest;
import com.example.driverservice.dto.response.CarPageResponse;
import com.example.driverservice.dto.response.CarResponse;
import com.example.driverservice.exception.CarNotFoundException;
import com.example.driverservice.exception.CarNumberUniqueException;
import com.example.driverservice.model.Car;
import com.example.driverservice.repository.CarRepository;
import com.example.driverservice.service.CarService;
import com.example.driverservice.util.FieldValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarServiceImpl implements CarService {
    private static final String CAR_NOT_FOUND = "Car with id '%s' not found";
    private static final String CAR_NUMBER_EXIST = "Car with number '%s' already exist";
    private final CarRepository carRepository;
    private final ModelMapper modelMapper;
    private final FieldValidator fieldValidator;

    @Override
    public CarResponse createCar(CarRequest carRequest) {
        log.info("Creating car with number: {}", carRequest);

        String carNumber = carRequest.getNumber();
        carRepository.findCarByNumber(carNumber)
                .ifPresent(car -> {
                    log.error("Car with number {} already exist", carNumber);
                    throw new CarNumberUniqueException(String.format(CAR_NUMBER_EXIST, carNumber));
                });

        Car newCar = mapCarRequestToCar(carRequest);
        newCar = carRepository.save(newCar);
        return mapCarToCarResponse(newCar);
    }

    @Override
    public CarResponse editCar(long id, CarRequest carRequest) {
        log.info("Updating car with id {}: {}", id, carRequest);

        Car existingCar = carRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Car with id {} not found", id);
                    return new CarNotFoundException(String.format(CAR_NOT_FOUND, id));
                });

        String carNumber = carRequest.getNumber();
        Optional<Car> optionalCar = carRepository.findCarByNumber(carNumber);
        if (optionalCar.isPresent() && optionalCar.get().getId() != id) {
            log.error("Car with number {} already exist", carNumber);
            throw new CarNumberUniqueException(String.format(CAR_NUMBER_EXIST, carNumber));
        }

        Car updatedCar = mapCarRequestToCar(carRequest);
        updatedCar.setId(existingCar.getId());
        updatedCar = carRepository.save(updatedCar);
        return mapCarToCarResponse(updatedCar);
    }

    @Override
    public CarResponse getCarById(long id) {
        log.info("Retrieving car by id: {}", id);

        return carRepository.findById(id)
                .map(this::mapCarToCarResponse)
                .orElseThrow(() -> {
                    log.error("Car with id {} not found", id);
                    return new CarNotFoundException(String.format(CAR_NOT_FOUND, id));
                });
    }

    @Override
    public CarPageResponse getAllCars(int page, int size, String sortBy) {
        log.info("Retrieving all cars with pagination: page={}, size={}, sortBy={}", page, size, sortBy);

        fieldValidator.checkSortField(Car.class, sortBy);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<Car> carPage = carRepository.findAll(pageable);

        List<CarResponse> carResponses = carPage.getContent()
                .stream()
                .map(this::mapCarToCarResponse)
                .toList();

        return CarPageResponse.builder()
                .cars(carResponses)
                .totalPages(carPage.getTotalPages())
                .totalElements(carPage.getTotalElements())
                .currentPage(carPage.getNumber())
                .pageSize(carPage.getSize())
                .build();
    }

    public Car mapCarRequestToCar(CarRequest carRequest) {
        return modelMapper.map(carRequest, Car.class);
    }

    public CarResponse mapCarToCarResponse(Car car) {
        return modelMapper.map(car, CarResponse.class);
    }
}
