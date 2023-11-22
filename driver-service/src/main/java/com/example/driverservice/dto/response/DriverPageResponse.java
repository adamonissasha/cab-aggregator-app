package com.example.driverservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class DriverPageResponse {
    private List<DriverResponse> drivers;
    private int totalPages;
    private long totalElements;
    private int currentPage;
    private int pageSize;
}
