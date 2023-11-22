package com.example.ridesservice.dto.response;

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
public class RidesPageResponse {
    private List<RideResponse> rides;
    private int totalPages;
    private long totalElements;
    private int currentPage;
    private int pageSize;
}
