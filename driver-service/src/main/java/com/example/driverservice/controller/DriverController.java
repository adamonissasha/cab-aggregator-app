package com.example.driverservice.controller;

import com.example.driverservice.dto.request.DriverRequest;
import com.example.driverservice.dto.response.DriverResponse;
import com.example.driverservice.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/driver")
@RequiredArgsConstructor
public class DriverController {
    private final DriverService driverService;

    @PostMapping()
    public DriverResponse createPassenger(@RequestBody DriverRequest driverRequest) {
        return driverService.createDriver(driverRequest);
    }

    @PutMapping("/{id}")
    public DriverResponse editPassenger(@RequestBody DriverRequest driverRequest,
                                           @PathVariable("id") long id) {
        return driverService.editPassenger(id, driverRequest);
    }

    @GetMapping("/{id}")
    public DriverResponse getDriverById(@PathVariable("id") long id) {
        return driverService.getDriverById(id);
    }
}
