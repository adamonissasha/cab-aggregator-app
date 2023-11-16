package com.example.driverservice.service;

import com.example.driverservice.dto.request.CarRequest;
import com.example.driverservice.dto.response.CarResponse;
import org.springframework.data.domain.Page;

public interface CarService {
    CarResponse createCar(CarRequest carRequest);

    CarResponse editCar(long id, CarRequest carRequest);

    CarResponse getCarById(long id);

    Page<CarResponse> getAllCars(int page, int size, String sortBy);
}
