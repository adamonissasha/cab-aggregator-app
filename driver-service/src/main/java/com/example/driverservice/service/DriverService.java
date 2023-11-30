package com.example.driverservice.service;

import com.example.driverservice.dto.request.DriverRequest;
import com.example.driverservice.dto.response.DriverPageResponse;
import com.example.driverservice.dto.response.DriverResponse;
import com.example.driverservice.dto.response.RideDriverResponse;

public interface DriverService {
    DriverResponse createDriver(DriverRequest driverRequest);

    DriverResponse editDriver(long id, DriverRequest driverRequest);

    DriverResponse getDriverById(long id);

    DriverPageResponse getAllDrivers(int page, int size, String sortBy);

    RideDriverResponse getFreeDriver();

    DriverResponse changeDriverStatusToFree(Long id);
}