package com.example.driverservice.controller;

import com.example.driverservice.dto.request.CarRequest;
import com.example.driverservice.dto.response.CarResponse;
import com.example.driverservice.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/driver/car")
@RequiredArgsConstructor
public class CarController {
    private final CarService carService;

    @PostMapping()
    public CarResponse createCar(@RequestBody CarRequest carRequest) {
        return carService.createCar(carRequest);
    }

    @PutMapping("/{id}")
    public CarResponse editCar(@RequestBody CarRequest carRequest,
                               @PathVariable("id") long id) {
        return carService.editCar(id, carRequest);
    }

    @GetMapping("/{id}")
    public CarResponse getCarById(@PathVariable("id") long id) {
        return carService.getCarById(id);
    }
}
