package com.example.ridesservice.service.impl;

import com.example.ridesservice.dto.request.ConfirmRideRequest;
import com.example.ridesservice.dto.request.CreateRideRequest;
import com.example.ridesservice.dto.request.EditRideRequest;
import com.example.ridesservice.dto.response.RideResponse;
import com.example.ridesservice.dto.response.StopResponse;
import com.example.ridesservice.exception.IncorrectFieldNameException;
import com.example.ridesservice.exception.IncorrectPaymentMethodException;
import com.example.ridesservice.exception.RideNotFoundException;
import com.example.ridesservice.exception.RideStatusException;
import com.example.ridesservice.model.PromoCode;
import com.example.ridesservice.model.Ride;
import com.example.ridesservice.model.enums.PaymentMethod;
import com.example.ridesservice.model.enums.RideStatus;
import com.example.ridesservice.repository.RideRepository;
import com.example.ridesservice.service.PromoCodeService;
import com.example.ridesservice.service.RideService;
import com.example.ridesservice.service.StopService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class RideServiceImpl implements RideService {
    private static final String INCORRECT_PAYMENT_METHOD = "Incorrect payment method!";
    private static final String RIDE_NOT_FOUND = "Ride not found!";
    private static final String RIDE_COMPLETED = "The ride has already been completed!";
    private static final String RIDE_CANCELED = "The ride has already been canceled!";
    private static final String RIDE_CONFIRMED = "The ride has already been confirmed!";
    private static final String RIDE_NOT_CONFIRMED = "The ride hasn't confirmed!";
    private static final String RIDE_STARTED = "The ride has already started!";
    private static final String RIDE_NOT_STARTED = "The ride hasn't started!";
    private static final String INCORRECT_FIELDS = "Invalid sortBy field. Allowed fields: ";
    private final StopService stopService;
    private final PromoCodeService promoCodeService;
    private final RideRepository rideRepository;
    private final ModelMapper modelMapper;
    private final Random random = new Random();

    @Override
    public RideResponse createRide(CreateRideRequest createRideRequest) {
        PaymentMethod paymentMethod = getPaymentMethod(createRideRequest.getPaymentMethod());
        PromoCode promoCode = promoCodeService.getPromoCodeByName(createRideRequest.getPromoCode());
        BigDecimal price = calculatePrice(promoCode);

        Ride newRide = Ride.builder()
                .passengerId(createRideRequest.getPassengerId())
                .startAddress(createRideRequest.getStartAddress())
                .endAddress(createRideRequest.getEndAddress())
                .paymentMethod(paymentMethod)
                .promoCode(promoCode)
                .creationDateTime(LocalDateTime.now())
                .price(price)
                .status(RideStatus.CREATED)
                .build();

        newRide = rideRepository.save(newRide);
        List<StopResponse> stops = stopService.createStops(createRideRequest.getStops(), newRide);

        return mapRideToRideResponse(newRide, stops);
    }

    @Override
    public RideResponse editRide(Long rideId, EditRideRequest editRideRequest) {
        Ride existingRide = getExistingRide(rideId);

        checkRideStatusNotEquals(existingRide, RideStatus.COMPLETED, RIDE_COMPLETED);
        checkRideStatusNotEquals(existingRide, RideStatus.CANCELED, RIDE_CANCELED);

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

        return mapRideToRideResponse(existingRide, stops);
    }

    @Override
    public RideResponse canselRide(Long rideId) {
        Ride existingRide = getExistingRide(rideId);

        checkRideStatusNotEquals(existingRide, RideStatus.CANCELED, RIDE_CANCELED);
        checkRideStatusNotEquals(existingRide, RideStatus.COMPLETED, RIDE_COMPLETED);
        checkRideStatusNotEquals(existingRide, RideStatus.STARTED, RIDE_STARTED);

        existingRide.setStatus(RideStatus.CANCELED);
        rideRepository.save(existingRide);

        return mapRideToRideResponse(existingRide, stopService.getRideStops(existingRide));
    }

    @Override
    public RideResponse confirmRide(Long rideId, ConfirmRideRequest confirmRideRequest) {
        Ride ride = getExistingRide(rideId);

        checkRideStatusNotEquals(ride, RideStatus.COMPLETED, RIDE_COMPLETED);
        checkRideStatusNotEquals(ride, RideStatus.CANCELED, RIDE_CANCELED);
        checkRideStatusNotEquals(ride, RideStatus.CONFIRMED, RIDE_CONFIRMED);
        checkRideStatusNotEquals(ride, RideStatus.STARTED, RIDE_STARTED);

        ride.setStatus(RideStatus.CONFIRMED);
        ride.setDriverId(confirmRideRequest.getDriverId());
        ride.setDriverName(confirmRideRequest.getDriverName());
        ride.setCarNumber(confirmRideRequest.getCarNumber());
        ride.setCarColor(confirmRideRequest.getCarColor());
        ride.setCarMake(confirmRideRequest.getCarMake());
        rideRepository.save(ride);

        return mapRideToRideResponse(ride, stopService.getRideStops(ride));
    }

    @Override
    public RideResponse startRide(Long rideId) {
        Ride ride = getExistingRide(rideId);

        checkRideStatusNotEquals(ride, RideStatus.COMPLETED, RIDE_COMPLETED);
        checkRideStatusNotEquals(ride, RideStatus.CANCELED, RIDE_CANCELED);
        checkRideStatusNotEquals(ride, RideStatus.STARTED, RIDE_STARTED);
        if (!ride.getStatus().equals(RideStatus.CONFIRMED)) {
            throw new RideStatusException(RIDE_NOT_CONFIRMED);
        }

        ride.setStatus(RideStatus.STARTED);
        ride.setStartDateTime(LocalDateTime.now());
        rideRepository.save(ride);

        return mapRideToRideResponse(ride, stopService.getRideStops(ride));
    }

    @Override
    public RideResponse completeRide(Long rideId) {
        Ride ride = getExistingRide(rideId);

        checkRideStatusNotEquals(ride, RideStatus.COMPLETED, RIDE_COMPLETED);
        checkRideStatusNotEquals(ride, RideStatus.CANCELED, RIDE_CANCELED);
        if (!ride.getStatus().equals(RideStatus.STARTED)) {
            throw new RideStatusException(RIDE_NOT_STARTED);
        }

        ride.setStatus(RideStatus.COMPLETED);
        ride.setEndDateTime(LocalDateTime.now());
        rideRepository.save(ride);

        return mapRideToRideResponse(ride, stopService.getRideStops(ride));
    }

    @Override
    public RideResponse getRideByRideId(Long rideId) {
        Ride ride = getExistingRide(rideId);
        return mapRideToRideResponse(ride, stopService.getRideStops(ride));
    }

    @Override
    public Page<RideResponse> getAvailableRides(int page, int size, String sortBy) {
        checkSortField(sortBy);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return rideRepository.findAllByStatus(RideStatus.CREATED, pageable)
                .map(ride -> mapRideToRideResponse(ride, stopService.getRideStops(ride)));
    }

    @Override
    public Page<RideResponse> getPassengerRides(Long passengerId, int page, int size, String sortBy) {
        checkSortField(sortBy);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return rideRepository.findAllByPassengerId(passengerId, pageable)
                .map(ride -> mapRideToRideResponse(ride, stopService.getRideStops(ride)));
    }

    @Override
    public Page<RideResponse> getDriverRides(Long driverId, int page, int size, String sortBy) {
        checkSortField(sortBy);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return rideRepository.findAllByDriverId(driverId, pageable)
                .map(ride -> mapRideToRideResponse(ride, stopService.getRideStops(ride)));
    }


    private PaymentMethod getPaymentMethod(String paymentMethod) {
        if (!PaymentMethod.isValidPaymentMethod(paymentMethod)) {
            throw new IncorrectPaymentMethodException(INCORRECT_PAYMENT_METHOD);
        }
        return PaymentMethod.valueOf(paymentMethod);
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

    private RideResponse mapRideToRideResponse(Ride ride, List<StopResponse> stops) {
        RideResponse rideResponse = modelMapper.map(ride, RideResponse.class);
        PromoCode promoCode = ride.getPromoCode();
        if (promoCode != null) {
            rideResponse.setPromoCode(promoCode.getCode());
        }
        rideResponse.setStops(stops);
        return rideResponse;
    }

    private Ride getExistingRide(long rideId) {
        return rideRepository.findById(rideId)
                .orElseThrow(() -> new RideNotFoundException(RIDE_NOT_FOUND));
    }

    private void checkRideStatusNotEquals(Ride ride, RideStatus rideStatus, String message) {
        if (ride.getStatus().equals(rideStatus)) {
            throw new RideStatusException(message);
        }
    }

    public void checkSortField(String sortBy) {
        List<String> allowedSortFields = new ArrayList<>();
        getFieldNamesRecursive(Ride.class, allowedSortFields);
        if (!allowedSortFields.contains(sortBy)) {
            throw new IncorrectFieldNameException(INCORRECT_FIELDS + allowedSortFields);
        }
    }

    private static void getFieldNamesRecursive(Class<?> myClass, List<String> fieldNames) {
        if (myClass != null) {
            Field[] fields = myClass.getDeclaredFields();
            for (Field field : fields) {
                fieldNames.add(field.getName());
            }
            getFieldNamesRecursive(myClass.getSuperclass(), fieldNames);
        }
    }
}
