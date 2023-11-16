package com.example.driverservice.service.impl;

import com.example.driverservice.dto.request.CarRequest;
import com.example.driverservice.dto.response.CarResponse;
import com.example.driverservice.exception.CarNotFoundException;
import com.example.driverservice.exception.CarNumberUniqueException;
import com.example.driverservice.exception.IncorrectFieldNameException;
import com.example.driverservice.model.Car;
import com.example.driverservice.repository.CarRepository;
import com.example.driverservice.service.CarService;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private static final String CAR_NOT_FOUND = "Car not found!";
    private static final String CAR_NUMBER_EXIST = "Car with this number already exist!";
    private static final String INCORRECT_FIELDS = "Invalid sortBy field. Allowed fields: ";
    private final CarRepository carRepository;
    private final ModelMapper modelMapper;

    @Override
    public CarResponse createCar(CarRequest carRequest) {
        carRepository.findCarByNumber(carRequest.getNumber())
                .ifPresent(car -> {
                    throw new CarNumberUniqueException(CAR_NUMBER_EXIST);
                });
        Car newCar = mapCarRequestToCar(carRequest);
        newCar = carRepository.save(newCar);
        return mapCarToCarResponse(newCar);
    }

    @Override
    public CarResponse editCar(long id, CarRequest carRequest) {
        Car existingCar = carRepository.findById(id)
                .orElseThrow(() -> new CarNotFoundException(CAR_NOT_FOUND));
        Optional<Car> optionalCar = carRepository.findCarByNumber(carRequest.getNumber());
        if (optionalCar.isPresent() && optionalCar.get().getId() != id) {
            throw new CarNumberUniqueException(CAR_NUMBER_EXIST);
        }
        Car updatedCar = mapCarRequestToCar(carRequest);
        updatedCar.setId(existingCar.getId());
        updatedCar = carRepository.save(updatedCar);
        return mapCarToCarResponse(updatedCar);
    }

    @Override
    public CarResponse getCarById(long id) {
        return carRepository.findById(id)
                .map(this::mapCarToCarResponse)
                .orElseThrow(() -> new CarNotFoundException(CAR_NOT_FOUND));
    }

    @Override
    public Page<CarResponse> getAllCars(int page, int size, String sortBy) {
        checkSortField(sortBy);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return carRepository.findAll(pageable)
                .map(this::mapCarToCarResponse);
    }


    public void checkSortField(String sortBy) {
        List<String> allowedSortFields = new ArrayList<>();
        getFieldNamesRecursive(Car.class, allowedSortFields);
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

    public Car mapCarRequestToCar(CarRequest carRequest) {
        return modelMapper.map(carRequest, Car.class);
    }

    public CarResponse mapCarToCarResponse(Car car) {
        return modelMapper.map(car, CarResponse.class);
    }
}