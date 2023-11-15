package com.example.ridesservice.service;

import com.example.ridesservice.dto.request.PromoCodeRequest;
import com.example.ridesservice.dto.response.PromoCodeResponse;

import java.util.List;

public interface PromoCodeService {
    PromoCodeResponse createPromoCode(PromoCodeRequest promoCodeRequest);

    PromoCodeResponse editPromoCode(long id, PromoCodeRequest promoCodeRequest);

    PromoCodeResponse getPromoCodeById(long id);

    List<PromoCodeResponse> getAllPromoCodes();
}
