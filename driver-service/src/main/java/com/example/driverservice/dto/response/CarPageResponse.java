package com.example.driverservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CarPageResponse {
    private List<CarResponse> cars;
    private int totalPages;
    private long totalElements;
    private int currentPage;
    private int pageSize;
}
