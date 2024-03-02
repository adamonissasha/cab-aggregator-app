package com.example.ridesservice.service.impl;

import com.example.ridesservice.dto.request.PromoCodeRequest;
import com.example.ridesservice.dto.response.AllPromoCodesResponse;
import com.example.ridesservice.dto.response.PromoCodeResponse;
import com.example.ridesservice.exception.IncorrectDateException;
import com.example.ridesservice.exception.PromoCodeAlreadyExistsException;
import com.example.ridesservice.exception.PromoCodeNotFoundException;
import com.example.ridesservice.model.PromoCode;
import com.example.ridesservice.repository.PromoCodeRepository;
import com.example.ridesservice.service.PromoCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromoCodeServiceImpl implements PromoCodeService {
    private static final String PROMO_CODE_BY_ID_NOT_FOUND = "Promo code with id '%s' not found";
    private static final String PROMO_CODE_BY_CODE_NOT_FOUND = "Promo code '%s' not found";
    private static final String PROMO_CODE_ALREADY_EXISTS = "Promo code '%s' already exists in this period of time";
    private static final String INCORRECT_DATE = "Start date '%s' must not be after end date '%s'";
    private final PromoCodeRepository promoCodeRepository;
    private final ModelMapper modelMapper;

    @Override
    public PromoCodeResponse createPromoCode(PromoCodeRequest promoCodeRequest) {
        log.info("Creating promo code: {}", promoCodeRequest);

        LocalDate startDate = promoCodeRequest.getStartDate();
        LocalDate endDate = promoCodeRequest.getEndDate();
        String code = promoCodeRequest.getCode();

        Optional<PromoCode> optionalPromoCode = promoCodeRepository.findByCode(code);
        if (optionalPromoCode.isPresent() && optionalPromoCode.get().getEndDate().isAfter(startDate)) {
            throw new PromoCodeAlreadyExistsException(String.format(PROMO_CODE_ALREADY_EXISTS, code));
        }

        if (startDate.isBefore(LocalDate.now()) || endDate.isBefore(startDate)) {
            throw new IncorrectDateException(String.format(INCORRECT_DATE, startDate, endDate));
        }

        PromoCode newPromoCode = mapPromoCodeRequestToPromoCode(promoCodeRequest);
        newPromoCode = promoCodeRepository.save(newPromoCode);
        return mapPromoCodeToPromoCodeResponse(newPromoCode);
    }

    @Override
    public PromoCodeResponse editPromoCode(long id, PromoCodeRequest promoCodeRequest) {
        log.info("Editing promo code with id {}", id);

        PromoCode existingPromoCode = promoCodeRepository.findById(id)
                .orElseThrow(() -> new PromoCodeNotFoundException(String.format(PROMO_CODE_BY_ID_NOT_FOUND, id)));

        LocalDate startDate = promoCodeRequest.getStartDate();
        LocalDate endDate = promoCodeRequest.getEndDate();
        String code = promoCodeRequest.getCode();
        if (startDate.isBefore(LocalDate.now()) || endDate.isBefore(startDate)) {
            throw new IncorrectDateException(String.format(INCORRECT_DATE, startDate, endDate));
        }

        Optional<PromoCode> optionalPromoCode = promoCodeRepository.findByCode(code);
        if (optionalPromoCode.isPresent() && !optionalPromoCode.get().getId().equals(id)
                && optionalPromoCode.get().getEndDate().isAfter(startDate)) {
            throw new PromoCodeAlreadyExistsException(String.format(PROMO_CODE_ALREADY_EXISTS, code));
        }

        PromoCode updatedPromoCode = mapPromoCodeRequestToPromoCode(promoCodeRequest);
        updatedPromoCode.setId(existingPromoCode.getId());
        updatedPromoCode = promoCodeRepository.save(updatedPromoCode);
        return mapPromoCodeToPromoCodeResponse(updatedPromoCode);
    }

    @Override
    public PromoCodeResponse getPromoCodeById(long id) {
        log.info("Retrieving promo code by id: {}", id);

        return promoCodeRepository.findById(id)
                .map(this::mapPromoCodeToPromoCodeResponse)
                .orElseThrow(() -> new PromoCodeNotFoundException(String.format(PROMO_CODE_BY_ID_NOT_FOUND, id)));
    }

    @Override
    public AllPromoCodesResponse getAllPromoCodes() {
        log.info("Retrieving all promo codes");

        return AllPromoCodesResponse.builder()
                .promoCodes(promoCodeRepository.findAll()
                        .stream()
                        .map(this::mapPromoCodeToPromoCodeResponse)
                        .sorted(Comparator.comparingLong(PromoCodeResponse::getId))
                        .toList())
                .build();
    }

    @Override
    public PromoCode getPromoCodeByName(String promoCodeName) {
        log.info("Retrieving promo code by name {}", promoCodeName);

        return Optional.ofNullable(promoCodeName)
                .map(code -> promoCodeRepository.findByCode(code)
                        .filter(promoCode ->
                                LocalDate.now().isBefore(promoCode.getEndDate()) &&
                                        LocalDate.now().isAfter(promoCode.getStartDate()))
                        .orElseThrow(() -> new PromoCodeNotFoundException(String.format(PROMO_CODE_BY_CODE_NOT_FOUND, promoCodeName))))
                .orElse(null);
    }


    public PromoCode mapPromoCodeRequestToPromoCode(PromoCodeRequest promoCodeRequest) {
        return modelMapper.map(promoCodeRequest, PromoCode.class);
    }

    public PromoCodeResponse mapPromoCodeToPromoCodeResponse(PromoCode promoCode) {
        return modelMapper.map(promoCode, PromoCodeResponse.class);
    }
}
