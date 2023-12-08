package com.example.driverservice.service;

import com.example.driverservice.dto.request.DriverRequest;
import com.example.driverservice.dto.response.DriverPageResponse;
import com.example.driverservice.dto.response.DriverResponse;

public interface DriverService {
    DriverResponse createDriver(DriverRequest driverRequest);

    DriverResponse editDriver(long id, DriverRequest driverRequest);

    DriverResponse getDriverById(long id);

    DriverPageResponse getAllDrivers(int page, int size, String sortBy);

    DriverResponse getFreeDriver();

    DriverResponse changeDriverStatusToFree(Long id);

    void deleteDriverById(long id);
}