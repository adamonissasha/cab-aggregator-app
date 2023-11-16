package com.example.ridesservice.service.impl;

import com.example.ridesservice.dto.request.RideReservationRequest;
import com.example.ridesservice.dto.response.RideReservationResponse;
import com.example.ridesservice.dto.response.StopResponse;
import com.example.ridesservice.exception.IncorrectPaymentMethodException;
import com.example.ridesservice.exception.PromoCodeNotFoundException;
import com.example.ridesservice.exception.RideReservationNotFoundException;
import com.example.ridesservice.model.PromoCode;
import com.example.ridesservice.model.RideReservation;
import com.example.ridesservice.model.enums.PaymentMethod;
import com.example.ridesservice.model.enums.RideReservationStatus;
import com.example.ridesservice.repository.PromoCodeRepository;
import com.example.ridesservice.repository.RideReservationRepository;
import com.example.ridesservice.service.RideReservationService;
import com.example.ridesservice.service.StopService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class RideReservationServiceImpl implements RideReservationService {
    private static final String INCORRECT_PAYMENT_METHOD = "Incorrect payment method";
    private static final String PROMO_CODE_NOT_FOUND = "Promo code not found!";
    private static final String RIDE_RESERVATION_NOT_FOUND = "Ride reservation not found!";
    private final StopService stopService;
    private final PromoCodeRepository promoCodeRepository;
    private final RideReservationRepository rideReservationRepository;
    private final ModelMapper modelMapper;
    private final Random random = new Random();

    @Override
    public RideReservationResponse createRideReservation(RideReservationRequest rideReservationRequest,
                                                         Long passengerId) {
        PaymentMethod paymentMethod = getPaymentMethod(rideReservationRequest.getPaymentMethod());
        PromoCode promoCode = getPromoCode(rideReservationRequest.getPromoCode());
        double price = calculatePrice();
        if (promoCode != null) {
            price = applyPromoCode(price, promoCode);
        }
        RideReservation newRideReservation = RideReservation.builder()
                .passengerId(passengerId)
                .startAddress(rideReservationRequest.getStartAddress())
                .endAddress(rideReservationRequest.getEndAddress())
                .paymentMethod(paymentMethod)
                .promoCode(promoCode)
                .dateOfCreation(LocalDate.now())
                .timeOfCreation(LocalTime.now())
                .price(price)
                .status(RideReservationStatus.CREATED)
                .build();
        newRideReservation = rideReservationRepository.save(newRideReservation);
        List<StopResponse> stops = stopService.createStops(rideReservationRequest.getStops(), newRideReservation);
        RideReservationResponse rideReservationResponse = mapRideReservationToRideReservationResponse(newRideReservation);
        if (promoCode != null) {
            rideReservationResponse.setPromoCode(promoCode.getName());
        }
        rideReservationResponse.setStops(stops);
        return rideReservationResponse;
    }

    @Override
    public RideReservationResponse editRideReservation(Long id, Long passengerId, RideReservationRequest rideReservationRequest) {
        PaymentMethod paymentMethod = getPaymentMethod(rideReservationRequest.getPaymentMethod());
        RideReservation existingRideReservation = getExistingRideReservation(id);
        PromoCode promoCode = getPromoCode(rideReservationRequest.getPromoCode());
        Double price = existingRideReservation.getPrice();
        if (!existingRideReservation.getStartAddress().equals(rideReservationRequest.getStartAddress())
                || !existingRideReservation.getEndAddress().equals(rideReservationRequest.getEndAddress())) {
            price = calculatePrice();
        }
        if (promoCode != null) {
            price = applyPromoCode(price, promoCode);
        }
        existingRideReservation.setPrice(price);
        existingRideReservation.setStartAddress(rideReservationRequest.getStartAddress());
        existingRideReservation.setEndAddress(rideReservationRequest.getEndAddress());
        existingRideReservation.setPromoCode(promoCode);
        existingRideReservation.setPaymentMethod(paymentMethod);
        existingRideReservation = rideReservationRepository.save(existingRideReservation);
        List<StopResponse> stops = stopService.editStops(rideReservationRequest.getStops(), existingRideReservation);
        RideReservationResponse rideReservationResponse = mapRideReservationToRideReservationResponse(existingRideReservation);
        rideReservationResponse.setStops(stops);
        if (promoCode != null) {
            rideReservationResponse.setPromoCode(promoCode.getName());
        }
        return rideReservationResponse;
    }


    private PaymentMethod getPaymentMethod(String paymentMethod) {
        if (!PaymentMethod.isValidPaymentMethod(paymentMethod)) {
            throw new IncorrectPaymentMethodException(INCORRECT_PAYMENT_METHOD);
        }
        return PaymentMethod.valueOf(paymentMethod);
    }

    private RideReservation getExistingRideReservation(long id) {
        return rideReservationRepository.findById(id)
                .orElseThrow(() -> new RideReservationNotFoundException(RIDE_RESERVATION_NOT_FOUND));
    }

    private PromoCode getPromoCode(String promoCodeText) {
        if (promoCodeText == null) {
            return null;
        }
        PromoCode promoCode = promoCodeRepository.findByName(promoCodeText)
                .orElseThrow(() -> new PromoCodeNotFoundException(PROMO_CODE_NOT_FOUND));
        if (LocalDate.now().isAfter(promoCode.getEndDate()) && LocalDate.now().isBefore(promoCode.getStartDate())) {
            throw new PromoCodeNotFoundException(PROMO_CODE_NOT_FOUND);
        }
        return promoCode;
    }

    private Double calculatePrice() {
        int minPrice = 5;
        int maxPrice = 50;
        double price = minPrice + (maxPrice - minPrice) * random.nextDouble();
        return Math.round(price * 10.0) / 10.0;
    }

    private Double applyPromoCode(double price, PromoCode promoCode) {
        price -= price * (promoCode.getDiscountPercent() / 100.0);
        return Math.round(price * 10.0) / 10.0;
    }

    private RideReservationResponse mapRideReservationToRideReservationResponse(RideReservation rideReservation) {
        return modelMapper.map(rideReservation, RideReservationResponse.class);
    }
}
