package com.example.driverservice.service;

import com.example.driverservice.dto.request.DriverRequest;
import com.example.driverservice.dto.response.DriverResponse;
import org.springframework.data.domain.Page;

public interface DriverService {
    DriverResponse createDriver(DriverRequest driverRequest);

    DriverResponse editPassenger(long id, DriverRequest driverRequest);

    DriverResponse getDriverById(long id);

    Page<DriverResponse> getAllDrivers(int page, int size, String sortBy);
}
