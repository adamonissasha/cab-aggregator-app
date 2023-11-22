package com.example.driverservice.service;

import com.example.driverservice.dto.request.CarRequest;
import com.example.driverservice.dto.response.CarPageResponse;
import com.example.driverservice.dto.response.CarResponse;

public interface CarService {
    CarResponse createCar(CarRequest carRequest);

    CarResponse editCar(long id, CarRequest carRequest);

    CarResponse getCarById(long id);

    CarPageResponse getAllCars(int page, int size, String sortBy);
}
