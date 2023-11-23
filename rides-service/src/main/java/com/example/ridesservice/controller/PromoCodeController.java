package com.example.ridesservice.controller;

import com.example.ridesservice.dto.request.PromoCodeRequest;
import com.example.ridesservice.dto.response.PromoCodeResponse;
import com.example.ridesservice.service.PromoCodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ride/promo-code")
@RequiredArgsConstructor
public class PromoCodeController {
    private final PromoCodeService promoCodeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PromoCodeResponse createPromoCode(@Valid @RequestBody PromoCodeRequest promoCodeRequest) {
        return promoCodeService.createPromoCode(promoCodeRequest);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PromoCodeResponse editPromoCode(@Valid @RequestBody PromoCodeRequest promoCodeRequest,
                                           @PathVariable("id") long id) {
        return promoCodeService.editPromoCode(id, promoCodeRequest);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PromoCodeResponse getPromoCodeById(@PathVariable("id") long id) {
        return promoCodeService.getPromoCodeById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<PromoCodeResponse> getAllPromoCodes() {
        return promoCodeService.getAllPromoCodes();
    }
}
