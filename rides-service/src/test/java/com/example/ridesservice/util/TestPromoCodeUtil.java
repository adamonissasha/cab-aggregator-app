package com.example.ridesservice.util;


import com.example.ridesservice.dto.request.PromoCodeRequest;
import com.example.ridesservice.dto.response.AllPromoCodesResponse;
import com.example.ridesservice.dto.response.ExceptionResponse;
import com.example.ridesservice.dto.response.PromoCodeResponse;
import com.example.ridesservice.dto.response.ValidationErrorResponse;
import com.example.ridesservice.model.PromoCode;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@UtilityClass
public class TestPromoCodeUtil {
    private final Long FIRST_PROMO_CODE_ID = 98L;
    private final Long SECOND_PROMO_CODE_ID = 99L;
    private final Long INVALID_PROMO_CODE_ID = 199L;
    private final String FIRST_PROMO_CODE = "HELLO";
    private final String SECOND_PROMO_CODE = "NEWYEAR";
    private final String NEW_PROMO_CODE = "WINTER";
    private final LocalDate FIRST_PROMO_CODE_START_DATE = LocalDate.parse("2023-11-20");
    private final LocalDate SECOND_PROMO_CODE_START_DATE = LocalDate.now().minusDays(10);
    private final LocalDate NEW_PROMO_CODE_START_DATE = LocalDate.now().plusDays(2);
    private final LocalDate FIRST_PROMO_CODE_END_DATE = LocalDate.parse("2023-12-03");
    private final LocalDate SECOND_PROMO_CODE_END_DATE = LocalDate.now().plusDays(10);
    private final LocalDate NEW_PROMO_CODE_END_DATE = LocalDate.now().plusDays(8);
    private final LocalDate INVALID_PROMO_CODE_END_DATE = LocalDate.now().minusDays(8);
    private final Integer FIRST_PROMO_CODE_DISCOUNT_PERCENT = 10;
    private final Integer SECOND_PROMO_CODE_DISCOUNT_PERCENT = 15;
    private final Integer NEW_PROMO_CODE_DISCOUNT_PERCENT = 20;
    private final Integer INVALID_PROMO_CODE_DISCOUNT_PERCENT = 90;
    private final String PROMO_CODE_BY_ID_NOT_FOUND = "Promo code with id '%s' not found";
    private final String PROMO_CODE_ALREADY_EXISTS = "Promo code '%s' already exists in this period of time";
    private final String PROMO_CODE_REQUIRED = "Promo code is required";
    private final String START_DATE_NOT_NULL = "Start date of promo code cannot be null";
    private final String END_DATE_FUTURE = "End date of promo code  must be in the future";
    private final String DISCOUNT_PERCENT_MAX = "Promo code discount percent cannot be grater than 80";

    public Long getFirstPromoCodeId() {
        return FIRST_PROMO_CODE_ID;
    }

    public Long getInvalidId() {
        return INVALID_PROMO_CODE_ID;
    }

    public PromoCode getFirstPromoCode() {
        return PromoCode.builder()
                .id(FIRST_PROMO_CODE_ID)
                .code(FIRST_PROMO_CODE)
                .startDate(FIRST_PROMO_CODE_START_DATE)
                .endDate(FIRST_PROMO_CODE_END_DATE)
                .discountPercent(FIRST_PROMO_CODE_DISCOUNT_PERCENT)
                .build();
    }

    public PromoCode getSecondPromoCode() {
        return PromoCode.builder()
                .id(SECOND_PROMO_CODE_ID)
                .code(SECOND_PROMO_CODE)
                .startDate(SECOND_PROMO_CODE_START_DATE)
                .endDate(SECOND_PROMO_CODE_END_DATE)
                .discountPercent(SECOND_PROMO_CODE_DISCOUNT_PERCENT)
                .build();
    }

    public PromoCodeRequest getPromoCodeRequest() {
        return PromoCodeRequest.builder()
                .code(SECOND_PROMO_CODE)
                .startDate(NEW_PROMO_CODE_START_DATE)
                .endDate(NEW_PROMO_CODE_END_DATE)
                .discountPercent(SECOND_PROMO_CODE_DISCOUNT_PERCENT)
                .build();
    }

    public PromoCodeRequest getNewPromoCodeRequest() {
        return PromoCodeRequest.builder()
                .code(NEW_PROMO_CODE)
                .startDate(NEW_PROMO_CODE_START_DATE)
                .endDate(NEW_PROMO_CODE_END_DATE)
                .discountPercent(NEW_PROMO_CODE_DISCOUNT_PERCENT)
                .build();
    }

    public PromoCodeRequest getPromoCodeRequestWithInvalidData() {
        return PromoCodeRequest.builder()
                .code("")
                .startDate(null)
                .endDate(INVALID_PROMO_CODE_END_DATE)
                .discountPercent(INVALID_PROMO_CODE_DISCOUNT_PERCENT)
                .build();
    }

    public PromoCodeResponse getFirstPromoCodeResponse() {
        return PromoCodeResponse.builder()
                .id(FIRST_PROMO_CODE_ID)
                .code(FIRST_PROMO_CODE)
                .startDate(FIRST_PROMO_CODE_START_DATE)
                .endDate(FIRST_PROMO_CODE_END_DATE)
                .discountPercent(FIRST_PROMO_CODE_DISCOUNT_PERCENT)
                .build();
    }

    public PromoCodeResponse getSecondPromoCodeResponse() {
        return PromoCodeResponse.builder()
                .id(SECOND_PROMO_CODE_ID)
                .code(SECOND_PROMO_CODE)
                .startDate(SECOND_PROMO_CODE_START_DATE)
                .endDate(SECOND_PROMO_CODE_END_DATE)
                .discountPercent(SECOND_PROMO_CODE_DISCOUNT_PERCENT)
                .build();
    }

    public PromoCodeResponse getNewPromoCodeResponse() {
        return PromoCodeResponse.builder()
                .id(SECOND_PROMO_CODE_ID)
                .code(NEW_PROMO_CODE)
                .startDate(NEW_PROMO_CODE_START_DATE)
                .endDate(NEW_PROMO_CODE_END_DATE)
                .discountPercent(NEW_PROMO_CODE_DISCOUNT_PERCENT)
                .build();
    }

    public List<PromoCode> getPromoCodes() {
        return List.of(getFirstPromoCode(), getSecondPromoCode());
    }

    public List<PromoCodeResponse> getPromoCodeResponses() {
        return List.of(getFirstPromoCodeResponse(), getSecondPromoCodeResponse());
    }

    public ExceptionResponse getPromoCodeExistsExceptionResponse() {
        return ExceptionResponse.builder()
                .message(String.format(PROMO_CODE_ALREADY_EXISTS, SECOND_PROMO_CODE))
                .statusCode(HttpStatus.CONFLICT.value())
                .build();

    }

    public ExceptionResponse getPromoCodeNotFoundExceptionResponse() {
        return ExceptionResponse.builder()
                .message(String.format(PROMO_CODE_BY_ID_NOT_FOUND, INVALID_PROMO_CODE_ID))
                .statusCode(HttpStatus.NOT_FOUND.value())
                .build();
    }

    public ValidationErrorResponse getValidationErrorResponse() {
        List<String> errors = new ArrayList<>();
        errors.add(START_DATE_NOT_NULL);
        errors.add(END_DATE_FUTURE);
        errors.add(DISCOUNT_PERCENT_MAX);
        errors.add(PROMO_CODE_REQUIRED);
        Collections.sort(errors);

        return ValidationErrorResponse.builder()
                .errors(errors)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build();
    }

    public AllPromoCodesResponse getAllPromoCodesResponse() {
        List<PromoCodeResponse> promoCodeResponses = new ArrayList<>();
        promoCodeResponses.add(getFirstPromoCodeResponse());
        promoCodeResponses.add(getSecondPromoCodeResponse());
        promoCodeResponses.sort(Comparator.comparingLong(PromoCodeResponse::getId));

        return AllPromoCodesResponse.builder()
                .promoCodes(promoCodeResponses)
                .build();
    }
}