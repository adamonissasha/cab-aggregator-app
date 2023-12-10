package com.example.ridesservice.service.impl;

import com.example.ridesservice.dto.request.CreateRideRequest;
import com.example.ridesservice.dto.request.EditRideRequest;
import com.example.ridesservice.dto.request.RefillRequest;
import com.example.ridesservice.dto.request.WithdrawalRequest;
import com.example.ridesservice.dto.response.DriverResponse;
import com.example.ridesservice.dto.response.PassengerRideResponse;
import com.example.ridesservice.dto.response.PassengerRidesPageResponse;
import com.example.ridesservice.dto.response.RideResponse;
import com.example.ridesservice.dto.response.RidesPageResponse;
import com.example.ridesservice.dto.response.StopResponse;
import com.example.ridesservice.exception.IncorrectPaymentMethodException;
import com.example.ridesservice.exception.PaymentMethodException;
import com.example.ridesservice.exception.RideNotFoundException;
import com.example.ridesservice.exception.RideStatusException;
import com.example.ridesservice.exception.driver.FreeDriverNotFoundException;
import com.example.ridesservice.exception.passenger.PassengerException;
import com.example.ridesservice.mapper.RideMapper;
import com.example.ridesservice.model.PromoCode;
import com.example.ridesservice.model.Ride;
import com.example.ridesservice.model.enums.PaymentMethod;
import com.example.ridesservice.model.enums.RideStatus;
import com.example.ridesservice.repository.RideRepository;
import com.example.ridesservice.service.PromoCodeService;
import com.example.ridesservice.service.RideService;
import com.example.ridesservice.service.StopService;
import com.example.ridesservice.util.FieldValidator;
import com.example.ridesservice.webClient.BankWebClient;
import com.example.ridesservice.webClient.DriverWebClient;
import com.example.ridesservice.webClient.PassengerWebClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class RideServiceImpl implements RideService {
    private static final String INCORRECT_PAYMENT_METHOD = "'%s' - incorrect payment method";
    private static final String RIDE_NOT_FOUND = "Ride with id '%s' not found";
    private static final String RIDE_NOT_STARTED = "The ride with id '%s' hasn't started";
    private static final String PASSENGER_RIDE_EXCEPTION = "Passenger with id '%s' has already book a ride";
    private static final String CARD_PAYMENT_METHOD = "If you have chosen CARD payment method, select the card for payment";
    private static final String FREE_DRIVER_NOT_FOUND = "Free driver not found";
    private static final String REDIS_FREE_DRIVER_LIST_NAME = "freeDrivers";
    private final StopService stopService;
    private final PromoCodeService promoCodeService;
    private final RideRepository rideRepository;
    private final RideMapper rideMapper;
    private final DriverWebClient driverWebClient;
    private final BankWebClient bankWebClient;
    private final PassengerWebClient passengerWebClient;
    private final Random random = new Random();
    private final FieldValidator fieldValidator;
    private final ObjectMapper objectMapper;
    private final Jedis jedis;

    @Override
    public PassengerRideResponse createRide(CreateRideRequest createRideRequest) {
        PaymentMethod paymentMethod = getPaymentMethod(createRideRequest.getPaymentMethod());
        if (paymentMethod == PaymentMethod.CARD && createRideRequest.getBankCardId() == null) {
            throw new PaymentMethodException(CARD_PAYMENT_METHOD);
        }

        PromoCode promoCode = promoCodeService.getPromoCodeByName(createRideRequest.getPromoCode());
        BigDecimal price = calculatePrice(promoCode);
        Long passengerId = passengerWebClient.getPassenger(createRideRequest.getPassengerId()).getId();
        checkPassengerRides(passengerId);
        DriverResponse driver = getFreeDriver();

        Ride newRide = Ride.builder()
                .passengerId(passengerId)
                .startAddress(createRideRequest.getStartAddress())
                .endAddress(createRideRequest.getEndAddress())
                .paymentMethod(paymentMethod)
                .promoCode(promoCode)
                .driverId(driver.getId())
                .bankCardId(createRideRequest.getBankCardId())
                .carId(driver.getCar().getId())
                .creationDateTime(LocalDateTime.now())
                .price(price)
                .status(RideStatus.CREATED)
                .build();
        newRide = rideRepository.save(newRide);
        List<StopResponse> stops = stopService.createStops(createRideRequest.getStops(), newRide);

        return rideMapper.mapRideToPassengerRideResponse(newRide, stops, driver, driver.getCar());
    }

    @Override
    public PassengerRideResponse editRide(Long rideId, EditRideRequest editRideRequest) {
        Ride existingRide = getExistingRide(rideId);

        checkRideStatusNotEquals(existingRide, Arrays.asList(
                RideStatus.COMPLETED,
                RideStatus.CANCELED)
        );

        DriverResponse driver = driverWebClient.getDriver(existingRide.getDriverId());

        String newStartAddress = editRideRequest.getStartAddress();
        String newEndAddress = editRideRequest.getEndAddress();
        PaymentMethod paymentMethod = getPaymentMethod(editRideRequest.getPaymentMethod());

        if (!existingRide.getStartAddress().equals(newStartAddress)
                || !existingRide.getEndAddress().equals(newEndAddress)) {
            existingRide.setPrice(calculatePrice(existingRide.getPromoCode()));
        }
        existingRide.setStartAddress(newStartAddress);
        existingRide.setEndAddress(newEndAddress);
        existingRide.setPaymentMethod(paymentMethod);
        existingRide = rideRepository.save(existingRide);
        List<StopResponse> stops = stopService.editStops(editRideRequest.getStops(), existingRide);

        return rideMapper.mapRideToPassengerRideResponse(existingRide, stops, driver, driver.getCar());
    }

    @Override
    public RideResponse getRideByRideId(Long rideId) {
        Ride ride = getExistingRide(rideId);
        return rideMapper.mapRideToRideResponse(ride,
                stopService.getRideStops(ride));
    }

    @Override
    public PassengerRidesPageResponse getPassengerRides(Long passengerId, int page, int size, String sortBy) {
        fieldValidator.checkSortField(Ride.class, sortBy);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<Ride> ridesPage = rideRepository.findAllByPassengerId(passengerId, pageable);
        return rideMapper.mapRidesPageToPassengerRidesPageResponse(ridesPage);
    }

    @Override
    public RidesPageResponse getDriverRides(Long driverId, int page, int size, String sortBy) {
        fieldValidator.checkSortField(Ride.class, sortBy);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<Ride> ridesPage = rideRepository.findAllByDriverId(driverId, pageable);
        return rideMapper.mapRidesPageToRidesPageResponse(ridesPage);
    }


    @Override
    public RideResponse canselRide(Long rideId) {
        Ride existingRide = getExistingRide(rideId);

        checkRideStatusNotEquals(existingRide, Arrays.asList(
                RideStatus.COMPLETED,
                RideStatus.CANCELED,
                RideStatus.STARTED)
        );

        existingRide.setStatus(RideStatus.CANCELED);
        rideRepository.save(existingRide);

        return rideMapper.mapRideToRideResponse(existingRide, stopService.getRideStops(existingRide));
    }

    @Override
    public RideResponse startRide(Long rideId) {
        Ride ride = getExistingRide(rideId);

        checkRideStatusNotEquals(ride, Arrays.asList(
                RideStatus.COMPLETED,
                RideStatus.CANCELED,
                RideStatus.STARTED)
        );

        ride.setStatus(RideStatus.STARTED);
        ride.setStartDateTime(LocalDateTime.now());
        rideRepository.save(ride);

        return rideMapper.mapRideToRideResponse(ride, stopService.getRideStops(ride));
    }

    @Override
    @Transactional
    public RideResponse completeRide(Long rideId) {
        Ride ride = getExistingRide(rideId);

        checkRideStatusNotEquals(ride, Arrays.asList(
                RideStatus.COMPLETED,
                RideStatus.CANCELED)
        );
        if (ride.getStatus() != RideStatus.STARTED) {
            throw new RideStatusException(String.format(RIDE_NOT_STARTED, ride.getId()));
        }

        BigDecimal ridePrice = ride.getPrice();

        if (ride.getPaymentMethod() == PaymentMethod.CARD) {
            bankWebClient.withdrawalPaymentFromPassengerCard(ride.getBankCardId(),
                    WithdrawalRequest.builder()
                            .sum(ridePrice)
                            .build());
        }

        ride.setStatus(RideStatus.COMPLETED);
        ride.setEndDateTime(LocalDateTime.now());
        rideRepository.save(ride);

        Long driverId = ride.getDriverId();
        driverWebClient.changeDriverStatusToFree(driverId);
        bankWebClient.refillDriverBankAccount(RefillRequest.builder()
                .bankUserId(driverId)
                .sum(ridePrice)
                .build());

        return rideMapper.mapRideToRideResponse(ride, stopService.getRideStops(ride));
    }


    private BigDecimal calculatePrice(PromoCode promoCode) {
        int minPrice = 5;
        int maxPrice = 50;
        BigDecimal price = BigDecimal.valueOf(minPrice + (maxPrice - minPrice) * random.nextDouble());
        if (promoCode != null) {
            price = applyPromoCode(price, promoCode);
        }
        return price.setScale(1, RoundingMode.HALF_UP);
    }

    private BigDecimal applyPromoCode(BigDecimal price, PromoCode promoCode) {
        BigDecimal discount = BigDecimal.valueOf(promoCode.getDiscountPercent() / 100.0);
        price = price.subtract(price.multiply(discount));
        return price;
    }

    private PaymentMethod getPaymentMethod(String paymentMethod) {
        if (!PaymentMethod.isValidPaymentMethod(paymentMethod)) {
            throw new IncorrectPaymentMethodException(String.format(INCORRECT_PAYMENT_METHOD, paymentMethod));
        }
        return PaymentMethod.valueOf(paymentMethod);
    }

    private void checkPassengerRides(Long passengerId) {
        if (rideRepository.findAll()
                .stream()
                .filter(ride -> ride.getPassengerId().equals(passengerId))
                .anyMatch(ride -> ride.getStatus() == RideStatus.CREATED ||
                        ride.getStatus() == RideStatus.STARTED)) {
            throw new PassengerException(String.format(PASSENGER_RIDE_EXCEPTION, passengerId));
        }
    }

    private DriverResponse getFreeDriver() {
        DriverResponse driverResponse;
        String driverResponseJson = jedis.lpop(REDIS_FREE_DRIVER_LIST_NAME);
        if (driverResponseJson != null) {
            try {
                driverResponse = objectMapper.readValue(driverResponseJson, DriverResponse.class);
                return driverResponse;
            } catch (JsonProcessingException e) {
                throw new FreeDriverNotFoundException(FREE_DRIVER_NOT_FOUND);
            }
        } else {
            throw new FreeDriverNotFoundException(FREE_DRIVER_NOT_FOUND);
        }
    }

    private Ride getExistingRide(long rideId) {
        return rideRepository.findById(rideId)
                .orElseThrow(() -> new RideNotFoundException(String.format(RIDE_NOT_FOUND, rideId)));
    }

    private void checkRideStatusNotEquals(Ride ride, List<RideStatus> rideStatusList) {
        for (RideStatus status : rideStatusList) {
            if (ride.getStatus().equals(status)) {
                throw new RideStatusException(String.format(status.getStatusErrorMessage(), ride.getId()));
            }
        }
    }
}
