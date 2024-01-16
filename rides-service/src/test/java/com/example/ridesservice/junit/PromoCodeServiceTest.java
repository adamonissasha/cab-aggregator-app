package com.example.ridesservice.junit;

import com.example.ridesservice.dto.request.PromoCodeRequest;
import com.example.ridesservice.dto.response.AllPromoCodesResponse;
import com.example.ridesservice.dto.response.PromoCodeResponse;
import com.example.ridesservice.exception.IncorrectDateException;
import com.example.ridesservice.exception.PromoCodeAlreadyExistsException;
import com.example.ridesservice.exception.PromoCodeNotFoundException;
import com.example.ridesservice.model.PromoCode;
import com.example.ridesservice.repository.PromoCodeRepository;
import com.example.ridesservice.service.impl.PromoCodeServiceImpl;
import com.example.ridesservice.util.TestPromoCodeUtil;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PromoCodeServiceTest {
    @Mock
    PromoCodeRepository promoCodeRepository;
    @Mock
    ModelMapper modelMapper;
    @InjectMocks
    PromoCodeServiceImpl promoCodeService;

    @Test
    void testCreatePromoCode_WhenDatesAreValidAndPromoCodeUnique_ShouldCreatePromoCode() {
        PromoCodeRequest promoCodeRequest = TestPromoCodeUtil.getPromoCodeRequest();
        PromoCode promoCode = TestPromoCodeUtil.getFirstPromoCode();
        PromoCodeResponse expected = TestPromoCodeUtil.getFirstPromoCodeResponse();

        when(modelMapper.map(promoCodeRequest, PromoCode.class))
                .thenReturn(promoCode);
        when(promoCodeRepository.findByCode(promoCodeRequest.getCode()))
                .thenReturn(Optional.empty());
        when(promoCodeRepository.save(any(PromoCode.class)))
                .thenReturn(promoCode);
        when(modelMapper.map(promoCode, PromoCodeResponse.class))
                .thenReturn(expected);

        PromoCodeResponse actual = promoCodeService.createPromoCode(promoCodeRequest);

        assertEquals(expected, actual);

        verify(promoCodeRepository, times(1))
                .save(any(PromoCode.class));
        verify(promoCodeRepository, times(1))
                .findByCode(promoCodeRequest.getCode());
    }

    @Test
    void testCreatePromoCode_WhenDatesAreInvalid_ShouldThrowIncorrectDateException() {
        PromoCodeRequest promoCodeRequest = TestPromoCodeUtil.getPromoCodeRequest();
        promoCodeRequest.setStartDate(LocalDate.now().minusDays(10));

        assertThrows(IncorrectDateException.class, () -> promoCodeService.createPromoCode(promoCodeRequest));

        verify(promoCodeRepository, never())
                .save(any(PromoCode.class));
    }

    @Test
    void testCreatePromoCode_WhenPromoCodeAlreadyExistsWithLaterEndDate_ShouldThrowPromoCodeAlreadyExistsException() {
        PromoCodeRequest promoCodeRequest = TestPromoCodeUtil.getPromoCodeRequest();
        PromoCode existingPromoCode = TestPromoCodeUtil.getSecondPromoCode();

        when(promoCodeRepository.findByCode(existingPromoCode.getCode()))
                .thenReturn(Optional.of(existingPromoCode));

        assertThrows(PromoCodeAlreadyExistsException.class, () -> promoCodeService.createPromoCode(promoCodeRequest));

        verify(promoCodeRepository, never())
                .save(any(PromoCode.class));
        verify(promoCodeRepository, times(1))
                .findByCode(existingPromoCode.getCode());
    }

    @Test
    void testEditPromoCode_WhenEditingExistingPromoCode_ShouldEditAndReturnPromoCodeResponse() {
        Long id = TestPromoCodeUtil.getFirstPromoCodeId();
        PromoCodeRequest promoCodeRequest = TestPromoCodeUtil.getPromoCodeRequest();
        PromoCode existingPromoCode = TestPromoCodeUtil.getFirstPromoCode();
        PromoCode updatedPromoCode = TestPromoCodeUtil.getSecondPromoCode();
        PromoCodeResponse expected = TestPromoCodeUtil.getSecondPromoCodeResponse();

        when(modelMapper.map(promoCodeRequest, PromoCode.class))
                .thenReturn(updatedPromoCode);
        when(promoCodeRepository.findById(id))
                .thenReturn(Optional.of(existingPromoCode));
        when(promoCodeRepository.findByCode(updatedPromoCode.getCode()))
                .thenReturn(Optional.empty());
        when(promoCodeRepository.save(any(PromoCode.class)))
                .thenReturn(updatedPromoCode);
        when(modelMapper.map(updatedPromoCode, PromoCodeResponse.class))
                .thenReturn(expected);


        PromoCodeResponse actual = promoCodeService.editPromoCode(id, promoCodeRequest);

        assertEquals(expected, actual);

        verify(promoCodeRepository, times(1))
                .findById(id);
        verify(promoCodeRepository, times(1))
                .findByCode(updatedPromoCode.getCode());
        verify(promoCodeRepository, times(1))
                .save(any(PromoCode.class));
    }

    @Test
    void testEditPromoCode_WhenInvalidDatesProvided_ShouldThrowIncorrectDateException() {
        Long id = TestPromoCodeUtil.getFirstPromoCodeId();
        PromoCodeRequest promoCodeRequest = TestPromoCodeUtil.getPromoCodeRequest();
        promoCodeRequest.setStartDate(LocalDate.now().minusDays(5));
        PromoCode promoCode = TestPromoCodeUtil.getFirstPromoCode();

        when(promoCodeRepository.findById(id))
                .thenReturn(Optional.of(promoCode));
        assertThrows(IncorrectDateException.class, () -> promoCodeService.editPromoCode(id, promoCodeRequest));

        verify(promoCodeRepository, never())
                .save(any(PromoCode.class));
    }

    @Test
    void testEditPromoCode_WhenPromoCodeAlreadyExists_ShouldThrowPromoCodeAlreadyExistsException() {
        PromoCodeRequest promoCodeRequest = TestPromoCodeUtil.getPromoCodeRequest();
        PromoCode existingPromoCode = TestPromoCodeUtil.getFirstPromoCode();
        PromoCode newPromoCode = TestPromoCodeUtil.getSecondPromoCode();
        Long id = existingPromoCode.getId();

        when(promoCodeRepository.findById(id))
                .thenReturn(Optional.of(existingPromoCode));
        when(promoCodeRepository.findByCode(newPromoCode.getCode()))
                .thenReturn(Optional.of(newPromoCode));

        assertThrows(PromoCodeAlreadyExistsException.class, () -> promoCodeService.editPromoCode(id, promoCodeRequest));

        verify(promoCodeRepository, never())
                .save(any(PromoCode.class));
        verify(promoCodeRepository, times(1))
                .findById(id);
        verify(promoCodeRepository, times(1))
                .findByCode(newPromoCode.getCode());
    }

    @Test
    void testEditPromoCode_WhenPromoCodeNotFound_ShouldThrowPromoCodeNotFoundException() {
        Long id = TestPromoCodeUtil.getFirstPromoCodeId();
        PromoCodeRequest promoCodeRequest = TestPromoCodeUtil.getPromoCodeRequest();

        when(promoCodeRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(PromoCodeNotFoundException.class, () -> promoCodeService.editPromoCode(id, promoCodeRequest));

        verify(promoCodeRepository, never())
                .findByCode(anyString());
        verify(promoCodeRepository, never())
                .save(any(PromoCode.class));
        verify(promoCodeRepository, times(1))
                .findById(id);
    }

    @Test
    void testGetPromoCodeById_WhenPromoCodeExists_ShouldReturnPromoCodeResponse() {
        Long promoCodeId = TestPromoCodeUtil.getFirstPromoCodeId();
        PromoCode promoCode = TestPromoCodeUtil.getFirstPromoCode();
        PromoCodeResponse expected = TestPromoCodeUtil.getFirstPromoCodeResponse();

        when(promoCodeRepository.findById(promoCodeId))
                .thenReturn(Optional.of(promoCode));
        when(modelMapper.map(promoCode, PromoCodeResponse.class))
                .thenReturn(expected);

        PromoCodeResponse actual = promoCodeService.getPromoCodeById(promoCodeId);

        assertEquals(expected, actual);

        verify(promoCodeRepository, times(1))
                .findById(promoCodeId);
    }

    @Test
    void testGetPromoCodeById_WhenPromoCodeNotFound_ShouldThrowPromoCodeNotFoundException() {
        Long promoCodeId = TestPromoCodeUtil.getFirstPromoCodeId();

        when(promoCodeRepository.findById(promoCodeId))
                .thenReturn(Optional.empty());

        assertThrows(PromoCodeNotFoundException.class, () -> promoCodeService.getPromoCodeById(promoCodeId));

        verify(promoCodeRepository, times(1))
                .findById(promoCodeId);
    }

    @Test
    void testGetAllPromoCodes_WhenPromoCodesExist_ShouldReturnAllPromoCodes() {
        List<PromoCode> promoCodes = TestPromoCodeUtil.getPromoCodes();
        List<PromoCodeResponse> promoCodeResponses = TestPromoCodeUtil.getPromoCodeResponses();

        when(promoCodeRepository.findAll())
                .thenReturn(promoCodes);
        when(promoCodeService.mapPromoCodeToPromoCodeResponse(promoCodes.get(0)))
                .thenReturn(promoCodeResponses.get(0));
        when(promoCodeService.mapPromoCodeToPromoCodeResponse(promoCodes.get(1)))
                .thenReturn(promoCodeResponses.get(1));

        AllPromoCodesResponse expected = AllPromoCodesResponse.builder()
                .promoCodes(promoCodeResponses)
                .build();

        AllPromoCodesResponse actual = promoCodeService.getAllPromoCodes();

        assertEquals(expected, actual);

        verify(promoCodeRepository, times(1))
                .findAll();
    }

    @Test
    void testGetAllPromoCodes_WhenNoPromoCodesExist_ShouldReturnEmptyResponse() {
        List<PromoCodeResponse> expectedPromoCodeResponses = List.of();
        AllPromoCodesResponse expected = AllPromoCodesResponse.builder()
                .promoCodes(expectedPromoCodeResponses)
                .build();

        when(promoCodeRepository.findAll())
                .thenReturn(List.of());

        AllPromoCodesResponse actual = promoCodeService.getAllPromoCodes();

        assertEquals(expected, actual);
    }

    @Test
    void testGetPromoCodeByName_WhenPromoCodeExists_ShouldReturnPromoCode() {
        String code = TestPromoCodeUtil.getSecondPromoCode().getCode();
        PromoCode expected = TestPromoCodeUtil.getSecondPromoCode();
        expected.setStartDate(LocalDate.now().minusDays(5));
        expected.setEndDate(LocalDate.now().plusDays(5));

        when(promoCodeRepository.findByCode(code))
                .thenReturn(Optional.of(expected));

        PromoCode actual = promoCodeService.getPromoCodeByName(code);

        assertEquals(expected, actual);

        verify(promoCodeRepository, times(1))
                .findByCode(code);
    }

    @Test
    void testGetPromoCodeByName_WhenPromoCodeNotFound_ShouldThrowPromoCodeNotFoundException() {
        String promoCodeName = TestPromoCodeUtil.getFirstPromoCode().getCode();

        when(promoCodeRepository.findByCode(promoCodeName))
                .thenReturn(Optional.empty());

        assertThrows(PromoCodeNotFoundException.class, () -> promoCodeService.getPromoCodeByName(promoCodeName));

        verify(promoCodeRepository, times(1))
                .findByCode(promoCodeName);
    }

    @Test
    void testGetPromoCodeByName_WhenPromoCodeNameIsNull_ShouldReturnNull() {
        PromoCode actual = promoCodeService.getPromoCodeByName(null);

        assertNull(actual);
    }
}