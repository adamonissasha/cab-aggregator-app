package com.example.ridesservice.mapper;

import com.example.ridesservice.dto.response.CarResponse;
import com.example.ridesservice.dto.response.DriverResponse;
import com.example.ridesservice.dto.response.PassengerResponse;
import com.example.ridesservice.dto.response.PassengerRideResponse;
import com.example.ridesservice.dto.response.PassengerRidesPageResponse;
import com.example.ridesservice.dto.response.RideResponse;
import com.example.ridesservice.dto.response.RidesPageResponse;
import com.example.ridesservice.dto.response.StopResponse;
import com.example.ridesservice.model.PromoCode;
import com.example.ridesservice.model.Ride;
import com.example.ridesservice.service.StopService;
import com.example.ridesservice.webClient.DriverWebClient;
import com.example.ridesservice.webClient.PassengerWebClient;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RideMapper {
    private final ModelMapper modelMapper;
    private final StopService stopService;
    private final DriverWebClient driverWebClient;
    private final PassengerWebClient passengerWebClient;

    public RideResponse mapRideToRideResponse(Ride ride,
                                              List<StopResponse> stops) {
        DriverResponse driver = driverWebClient.getDriver(ride.getDriverId());
        CarResponse car = driverWebClient.getCar(ride.getCarId());
        PassengerResponse passenger = passengerWebClient.getPassenger(ride.getPassengerId());
        RideResponse rideResponse = modelMapper.map(ride, RideResponse.class);
        PromoCode promoCode = ride.getPromoCode();
        if (promoCode != null) {
            rideResponse.setPromoCode(promoCode.getCode());
        }
        rideResponse.setStatus(ride.getStatus().name());
        rideResponse.setStops(stops);
        rideResponse.setPassengerName(passenger.getFirstName());
        rideResponse.setPassengerPhoneNumber(passenger.getPhoneNumber());
        rideResponse.setPassengerRating(passenger.getRating());
        rideResponse.setDriverName(driver.getFirstName());
        rideResponse.setDriverPhoneNumber(driver.getPhoneNumber());
        rideResponse.setDriverRating(driver.getRating());
        rideResponse.setCarColor(car.getColor());
        rideResponse.setCarMake(car.getCarMake());
        rideResponse.setCarNumber(car.getNumber());
        return rideResponse;
    }

    public PassengerRideResponse mapRideToPassengerRideResponse(Ride ride,
                                                                List<StopResponse> stops,
                                                                DriverResponse driver,
                                                                CarResponse car) {
        PassengerRideResponse passengerRideResponse = modelMapper.map(ride, PassengerRideResponse.class);
        PromoCode promoCode = ride.getPromoCode();
        if (promoCode != null) {
            passengerRideResponse.setPromoCode(promoCode.getCode());
        }
        passengerRideResponse.setStatus(ride.getStatus().name());
        passengerRideResponse.setStops(stops);
        passengerRideResponse.setDriverName(driver.getFirstName());
        passengerRideResponse.setDriverPhoneNumber(driver.getPhoneNumber());
        passengerRideResponse.setDriverRating(driver.getRating());
        passengerRideResponse.setCarColor(car.getColor());
        passengerRideResponse.setCarMake(car.getCarMake());
        passengerRideResponse.setCarNumber(car.getNumber());
        return passengerRideResponse;
    }

    public PassengerRidesPageResponse mapRidesPageToPassengerRidesPageResponse(Page<Ride> ridesPage) {
        List<PassengerRideResponse> passengerRideResponses = ridesPage.getContent()
                .stream()
                .map(ride -> {
                    DriverResponse driver = driverWebClient.getDriver(ride.getDriverId());
                    CarResponse car = driverWebClient.getCar(ride.getCarId());
                    return mapRideToPassengerRideResponse(ride, stopService.getRideStops(ride).getStops(), driver, car);
                })
                .toList();

        return PassengerRidesPageResponse.builder()
                .rides(passengerRideResponses)
                .totalPages(ridesPage.getTotalPages())
                .totalElements(ridesPage.getTotalElements())
                .currentPage(ridesPage.getNumber())
                .pageSize(ridesPage.getSize())
                .build();
    }

    public RidesPageResponse mapRidesPageToRidesPageResponse(Page<Ride> ridesPage) {
        List<RideResponse> rideResponses = ridesPage.getContent()
                .stream()
                .map(ride -> mapRideToRideResponse(ride, stopService.getRideStops(ride).getStops()))
                .toList();

        return RidesPageResponse.builder()
                .rides(rideResponses)
                .totalPages(ridesPage.getTotalPages())
                .totalElements(ridesPage.getTotalElements())
                .currentPage(ridesPage.getNumber())
                .pageSize(ridesPage.getSize())
                .build();
    }
}
