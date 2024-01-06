package com.example.ridesservice.util;


import com.example.ridesservice.dto.request.PromoCodeRequest;
import com.example.ridesservice.dto.response.PromoCodeResponse;
import com.example.ridesservice.model.PromoCode;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TestPromoCodeUtil {
    static Long FIRST_PROMO_CODE_ID = 1L;
    static Long SECOND_PROMO_CODE_ID = 2L;
    static String FIRST_PROMO_CODE = "SUMMER";
    static String SECOND_PROMO_CODE = "WINTER";
    static LocalDate FIRST_PROMO_CODE_START_DATE = LocalDate.now().plusDays(2);
    static LocalDate SECOND_PROMO_CODE_START_DATE = LocalDate.now().plusDays(3);
    static LocalDate FIRST_PROMO_CODE_END_DATE = LocalDate.now().plusDays(8);
    static LocalDate SECOND_PROMO_CODE_END_DATE = LocalDate.now().plusDays(12);
    static Integer FIRST_PROMO_CODE_DISCOUNT_PERCENT = 30;
    static Integer SECOND_PROMO_CODE_DISCOUNT_PERCENT = 20;

    public static Long getFirstPromoCodeId() {
        return FIRST_PROMO_CODE_ID;
    }

    public static PromoCode getFirstPromoCode() {
        return PromoCode.builder()
                .id(FIRST_PROMO_CODE_ID)
                .code(FIRST_PROMO_CODE)
                .startDate(FIRST_PROMO_CODE_START_DATE)
                .endDate(FIRST_PROMO_CODE_END_DATE)
                .discountPercent(FIRST_PROMO_CODE_DISCOUNT_PERCENT)
                .build();
    }

    public static PromoCode getSecondPromoCode() {
        return PromoCode.builder()
                .id(SECOND_PROMO_CODE_ID)
                .code(SECOND_PROMO_CODE)
                .startDate(SECOND_PROMO_CODE_START_DATE)
                .endDate(SECOND_PROMO_CODE_END_DATE)
                .discountPercent(SECOND_PROMO_CODE_DISCOUNT_PERCENT)
                .build();
    }

    public static PromoCodeRequest getPromoCodeRequest() {
        return PromoCodeRequest.builder()
                .code(SECOND_PROMO_CODE)
                .startDate(SECOND_PROMO_CODE_START_DATE)
                .endDate(SECOND_PROMO_CODE_END_DATE)
                .discountPercent(SECOND_PROMO_CODE_DISCOUNT_PERCENT)
                .build();
    }

    public static PromoCodeResponse getFirstPromoCodeResponse() {
        return PromoCodeResponse.builder()
                .id(FIRST_PROMO_CODE_ID)
                .code(FIRST_PROMO_CODE)
                .startDate(FIRST_PROMO_CODE_START_DATE)
                .endDate(FIRST_PROMO_CODE_END_DATE)
                .discountPercent(FIRST_PROMO_CODE_DISCOUNT_PERCENT)
                .build();
    }

    public static PromoCodeResponse getSecondPromoCodeResponse() {
        return PromoCodeResponse.builder()
                .id(SECOND_PROMO_CODE_ID)
                .code(SECOND_PROMO_CODE)
                .startDate(SECOND_PROMO_CODE_START_DATE)
                .endDate(SECOND_PROMO_CODE_END_DATE)
                .discountPercent(SECOND_PROMO_CODE_DISCOUNT_PERCENT)
                .build();
    }

    public static List<PromoCode> getPromoCodes() {
        return List.of(getFirstPromoCode(), getSecondPromoCode());
    }

    public static List<PromoCodeResponse> getPromoCodeResponses() {
        return List.of(getFirstPromoCodeResponse(), getSecondPromoCodeResponse());
    }
}