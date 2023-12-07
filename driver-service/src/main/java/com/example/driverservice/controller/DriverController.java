package com.example.driverservice.controller;

import com.example.driverservice.dto.request.DriverRequest;
import com.example.driverservice.dto.response.DriverPageResponse;
import com.example.driverservice.dto.response.DriverResponse;
import com.example.driverservice.service.DriverService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/driver")
@RequiredArgsConstructor
public class DriverController {
    private final DriverService driverService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DriverResponse createPassenger(@Valid @RequestBody DriverRequest driverRequest) {
        return driverService.createDriver(driverRequest);
    }

    @PutMapping("/{id}")
    public DriverResponse editDriver(@Valid @RequestBody DriverRequest driverRequest,
                                     @PathVariable("id") long id) {
        return driverService.editDriver(id, driverRequest);
    }

    @GetMapping("/{id}")
    public DriverResponse getDriverById(@PathVariable("id") long id) {
        return driverService.getDriverById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteDriverById(@PathVariable("id") long id) {
        driverService.deleteDriverById(id);
    }

    @GetMapping
    public DriverPageResponse getAllDrivers(@RequestParam(defaultValue = "0") @Min(0) int page,
                                            @RequestParam(defaultValue = "10") @Min(1) int size,
                                            @RequestParam(defaultValue = "id") String sortBy) {
        return driverService.getAllDrivers(page, size, sortBy);
    }

    @GetMapping("/free")
    public DriverResponse getFreeDriver() {
        return driverService.getFreeDriver();
    }

    @PutMapping("/{id}/free")
    public DriverResponse changeStatusToFree(@PathVariable("id") Long id) {
        return driverService.changeDriverStatusToFree(id);
    }
}
