package com.example.driverservice.controller;

import com.example.driverservice.dto.request.CarRequest;
import com.example.driverservice.dto.response.CarResponse;
import com.example.driverservice.service.CarService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/driver/car")
@RequiredArgsConstructor
public class CarController {
    private final CarService carService;

    @PostMapping
    public CarResponse createCar(@Valid @RequestBody CarRequest carRequest) {
        return carService.createCar(carRequest);
    }

    @PutMapping("/{id}")
    public CarResponse editCar(@Valid @RequestBody CarRequest carRequest,
                               @PathVariable("id") long id) {
        return carService.editCar(id, carRequest);
    }

    @GetMapping("/{id}")
    public CarResponse getCarById(@PathVariable("id") long id) {
        return carService.getCarById(id);
    }

    @GetMapping
    public Page<CarResponse> getAllCars(@RequestParam(defaultValue = "0") @Min(0) int page,
                                        @RequestParam(defaultValue = "10") @Min(1) int size,
                                        @RequestParam(defaultValue = "id") String sortBy) {
        return carService.getAllCars(page, size, sortBy);
    }
}
