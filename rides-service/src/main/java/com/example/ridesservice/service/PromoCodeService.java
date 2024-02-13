package com.example.ridesservice.service;

import com.example.ridesservice.dto.request.PromoCodeRequest;
import com.example.ridesservice.dto.response.AllPromoCodesResponse;
import com.example.ridesservice.dto.response.PromoCodeResponse;
import com.example.ridesservice.model.PromoCode;

public interface PromoCodeService {
    PromoCodeResponse createPromoCode(PromoCodeRequest promoCodeRequest);

    PromoCodeResponse editPromoCode(long id, PromoCodeRequest promoCodeRequest);

    PromoCodeResponse getPromoCodeById(long id);

    AllPromoCodesResponse getAllPromoCodes();

    PromoCode getPromoCodeByName(String promoCodeName);
}
