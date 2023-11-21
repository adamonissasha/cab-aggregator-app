package com.example.ridesservice.model;

import com.example.ridesservice.model.enums.PaymentMethod;
import com.example.ridesservice.model.enums.RideStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "ride")
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String startAddress;

    private String endAddress;

    private Long passengerId;

    private PaymentMethod paymentMethod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promo_code_id")
    private PromoCode promoCode;

    private Double price;

    @Enumerated(value = EnumType.STRING)
    private RideStatus status;

    private LocalDateTime creationDateTime;

    private Long driverId;

    private String driverName;

    private String carNumber;

    private String carMake;

    private String carColor;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;
}
