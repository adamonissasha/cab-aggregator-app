package com.example.ridesservice.controller;

import com.example.ridesservice.dto.request.PromoCodeRequest;
import com.example.ridesservice.dto.response.PromoCodeResponse;
import com.example.ridesservice.service.PromoCodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ride/promo-code")
@RequiredArgsConstructor
public class PromoCodeController {
    private final PromoCodeService promoCodeService;

    @PostMapping()
    public PromoCodeResponse createPromoCode(@Valid @RequestBody PromoCodeRequest promoCodeRequest) {
        return promoCodeService.createPromoCode(promoCodeRequest);
    }

    @PutMapping("/{id}")
    public PromoCodeResponse editPromoCode(@Valid @RequestBody PromoCodeRequest promoCodeRequest,
                                           @PathVariable("id") long id) {
        return promoCodeService.editPromoCode(id, promoCodeRequest);
    }

    @GetMapping("/{id}")
    public PromoCodeResponse getPromoCodeById(@PathVariable("id") long id) {
        return promoCodeService.getPromoCodeById(id);
    }

    @GetMapping()
    public List<PromoCodeResponse> getAllPromoCodes() {
        return promoCodeService.getAllPromoCodes();
    }
}
