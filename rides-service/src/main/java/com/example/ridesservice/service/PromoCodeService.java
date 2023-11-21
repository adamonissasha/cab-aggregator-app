package com.example.ridesservice.service;

import com.example.ridesservice.dto.request.PromoCodeRequest;
import com.example.ridesservice.dto.response.PromoCodeResponse;
import com.example.ridesservice.model.PromoCode;

import java.util.List;

public interface PromoCodeService {
    PromoCodeResponse createPromoCode(PromoCodeRequest promoCodeRequest);

    PromoCodeResponse editPromoCode(long id, PromoCodeRequest promoCodeRequest);

    PromoCodeResponse getPromoCodeById(long id);

    List<PromoCodeResponse> getAllPromoCodes();

    PromoCode getPromoCodeByName(String promoCodeName);
}
