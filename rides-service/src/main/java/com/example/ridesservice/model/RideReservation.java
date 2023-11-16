package com.example.ridesservice.model;

import com.example.ridesservice.model.enums.PaymentMethod;
import com.example.ridesservice.model.enums.RideReservationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "ride_reservation")
public class RideReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dateOfCreation;

    private LocalTime timeOfCreation;

    private String startAddress;

    private String endAddress;

    private Double price;

    private RideReservationStatus status;

    private PaymentMethod paymentMethod;

    private Long passengerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promo_code_id")
    private PromoCode promoCode;
}
