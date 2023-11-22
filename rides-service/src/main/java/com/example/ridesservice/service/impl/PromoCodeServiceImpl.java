package com.example.ridesservice.service.impl;

import com.example.ridesservice.dto.request.PromoCodeRequest;
import com.example.ridesservice.dto.response.PromoCodeResponse;
import com.example.ridesservice.exception.IncorrectDateException;
import com.example.ridesservice.exception.PromoCodeAlreadyExistsException;
import com.example.ridesservice.exception.PromoCodeNotFoundException;
import com.example.ridesservice.model.PromoCode;
import com.example.ridesservice.repository.PromoCodeRepository;
import com.example.ridesservice.service.PromoCodeService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PromoCodeServiceImpl implements PromoCodeService {
    private static final String PROMO_CODE_NOT_FOUND = "Promo code not found!";
    private static final String PROMO_CODE_ALREADY_EXISTS = "Promo code already exists!";
    private static final String INCORRECT_DATE = "Incorrect date!";
    private final PromoCodeRepository promoCodeRepository;
    private final ModelMapper modelMapper;

    @Override
    public PromoCodeResponse createPromoCode(PromoCodeRequest promoCodeRequest) {
        LocalDate startDate = promoCodeRequest.getStartDate();
        LocalDate endDate = promoCodeRequest.getEndDate();
        if (startDate.isBefore(LocalDate.now()) || endDate.isBefore(startDate)) {
            throw new IncorrectDateException(INCORRECT_DATE);
        }
        Optional<PromoCode> optionalPromoCode =
                promoCodeRepository.findByCode(promoCodeRequest.getCode());
        if (optionalPromoCode.isPresent() && optionalPromoCode.get().getEndDate().isAfter(startDate)) {
            throw new PromoCodeAlreadyExistsException(PROMO_CODE_ALREADY_EXISTS);
        }
        PromoCode newPromoCode = mapPromoCodeRequestToPromoCode(promoCodeRequest);
        newPromoCode = promoCodeRepository.save(newPromoCode);
        return mapPromoCodeToPromoCodeResponse(newPromoCode);
    }

    @Override
    public PromoCodeResponse editPromoCode(long id, PromoCodeRequest promoCodeRequest) {
        LocalDate startDate = promoCodeRequest.getStartDate();
        LocalDate endDate = promoCodeRequest.getEndDate();
        if (startDate.isBefore(LocalDate.now()) || endDate.isBefore(startDate)) {
            throw new IncorrectDateException(INCORRECT_DATE);
        }
        PromoCode existingPromoCode = promoCodeRepository.findById(id)
                .orElseThrow(() -> new PromoCodeNotFoundException(PROMO_CODE_NOT_FOUND));
        Optional<PromoCode> optionalPromoCode =
                promoCodeRepository.findByCode(promoCodeRequest.getCode());
        if (optionalPromoCode.isPresent() && !optionalPromoCode.get().getId().equals(id)
                && optionalPromoCode.get().getEndDate().isAfter(startDate)) {
            throw new PromoCodeAlreadyExistsException(PROMO_CODE_ALREADY_EXISTS);
        }
        PromoCode updatedPromoCode = mapPromoCodeRequestToPromoCode(promoCodeRequest);
        updatedPromoCode.setId(existingPromoCode.getId());
        updatedPromoCode = promoCodeRepository.save(updatedPromoCode);
        return mapPromoCodeToPromoCodeResponse(updatedPromoCode);
    }

    @Override
    public PromoCodeResponse getPromoCodeById(long id) {
        return promoCodeRepository.findById(id)
                .map(this::mapPromoCodeToPromoCodeResponse)
                .orElseThrow(() -> new PromoCodeNotFoundException(PROMO_CODE_NOT_FOUND));
    }

    @Override
    public List<PromoCodeResponse> getAllPromoCodes() {
        return promoCodeRepository.findAll()
                .stream()
                .map(this::mapPromoCodeToPromoCodeResponse)
                .toList();
    }

    @Override
    public PromoCode getPromoCodeByName(String promoCodeName) {
        return Optional.ofNullable(promoCodeName)
                .map(code -> promoCodeRepository.findByCode(code)
                        .filter(promoCode ->
                                LocalDate.now().isAfter(promoCode.getEndDate()) &&
                                        LocalDate.now().isBefore(promoCode.getStartDate()))
                        .orElseThrow(() -> new PromoCodeNotFoundException(PROMO_CODE_NOT_FOUND)))
                .orElse(null);
    }


    public PromoCode mapPromoCodeRequestToPromoCode(PromoCodeRequest promoCodeRequest) {
        return modelMapper.map(promoCodeRequest, PromoCode.class);
    }

    public PromoCodeResponse mapPromoCodeToPromoCodeResponse(PromoCode promoCode) {
        return modelMapper.map(promoCode, PromoCodeResponse.class);
    }
}
