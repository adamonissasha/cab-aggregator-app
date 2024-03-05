package com.example.ridesservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RidesPageResponse {
    private List<RideResponse> rides;
    private int totalPages;
    private long totalElements;
    private int currentPage;
    private int pageSize;
}
