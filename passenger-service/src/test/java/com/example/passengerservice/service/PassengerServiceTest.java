package com.example.passengerservice.service;

import com.example.passengerservice.dto.request.PassengerRequest;
import com.example.passengerservice.dto.response.AveragePassengerRatingResponse;
import com.example.passengerservice.dto.response.PassengerPageResponse;
import com.example.passengerservice.dto.response.PassengerResponse;
import com.example.passengerservice.exception.PassengerNotFoundException;
import com.example.passengerservice.exception.PhoneNumberUniqueException;
import com.example.passengerservice.model.Passenger;
import com.example.passengerservice.repository.PassengerRepository;
import com.example.passengerservice.service.impl.PassengerServiceImpl;
import com.example.passengerservice.util.FieldValidator;
import com.example.passengerservice.util.TestPassengerUtil;
import com.example.passengerservice.webClient.BankWebClient;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PassengerServiceTest {
    @Mock
    PassengerRepository passengerRepository;
    @Mock
    ModelMapper modelMapper;
    @Mock
    FieldValidator fieldValidator;
    @Mock
    PassengerRatingService passengerRatingService;
    @Mock
    BankWebClient bankWebClient;
    @InjectMocks
    PassengerServiceImpl passengerService;

    @Test
    public void testCreatePassenger_WhenPhoneNumberUnique_ShouldCreatePassenger() {
        PassengerRequest passengerRequest = TestPassengerUtil.getPassengerRequest();
        Passenger newPassenger = TestPassengerUtil.getFirstPassenger();
        PassengerResponse expectedPassengerResponse = TestPassengerUtil.getPassengerResponse();
        AveragePassengerRatingResponse passengerRating = TestPassengerUtil.getPassengerRating();

        when(passengerRepository.findPassengerByPhoneNumber(passengerRequest.getPhoneNumber())).thenReturn(Optional.empty());
        when(modelMapper.map(any(PassengerRequest.class), eq(Passenger.class))).thenReturn(newPassenger);
        when(passengerRepository.save(any(Passenger.class))).thenReturn(TestPassengerUtil.getFirstPassenger());
        when(passengerRatingService.getAveragePassengerRating(anyLong())).thenReturn(passengerRating);
        when(modelMapper.map(any(Passenger.class), eq(PassengerResponse.class))).thenReturn(expectedPassengerResponse);

        PassengerResponse actualPassengerResponse = passengerService.createPassenger(passengerRequest);

        assertNotNull(actualPassengerResponse);
        assertEquals(expectedPassengerResponse, actualPassengerResponse);
        verify(passengerRepository, times(1)).save(any(Passenger.class));
        verify(passengerRatingService, times(1)).getAveragePassengerRating(anyLong());
    }

    @Test
    public void testCreatePassenger_WhenPhoneNumberAlreadyExists_ShouldThrowPhoneNumberUniqueException() {
        PassengerRequest passengerRequest = TestPassengerUtil.getPassengerRequest();
        Passenger existingPassenger = TestPassengerUtil.getFirstPassenger();
        String existingPassengerPhoneNumber = TestPassengerUtil.getFirstPassengerPhoneNumber();

        when(passengerRepository.findPassengerByPhoneNumber(existingPassengerPhoneNumber)).thenReturn(Optional.of(existingPassenger));

        assertThrows(PhoneNumberUniqueException.class, () -> passengerService.createPassenger(passengerRequest));
    }

    @Test
    public void testEditPassenger_WhenPassengerExistsAndPassengerPhoneNumberUnique_ShouldEditPassenger() {
        Long passengerId = TestPassengerUtil.getFirstPassengerId();
        PassengerRequest passengerRequest = TestPassengerUtil.getPassengerRequest();
        Passenger updatedPassenger = TestPassengerUtil.getFirstPassenger();
        Passenger existingPassenger = TestPassengerUtil.getSecondPassenger();
        PassengerResponse expectedPassengerResponse = TestPassengerUtil.getPassengerResponse();
        AveragePassengerRatingResponse passengerRating = TestPassengerUtil.getPassengerRating();

        when(passengerRepository.findById(passengerId)).thenReturn(Optional.of(existingPassenger));
        when(passengerRepository.findPassengerByPhoneNumber(passengerRequest.getPhoneNumber())).thenReturn(Optional.empty());
        when(modelMapper.map(passengerRequest, Passenger.class)).thenReturn(updatedPassenger);
        when(passengerRepository.save(updatedPassenger)).thenReturn(updatedPassenger);
        when(passengerRatingService.getAveragePassengerRating(anyLong())).thenReturn(passengerRating);
        when(modelMapper.map(updatedPassenger, PassengerResponse.class)).thenReturn(expectedPassengerResponse);

        PassengerResponse actualPassengerResponse = passengerService.editPassenger(passengerId, passengerRequest);

        assertNotNull(actualPassengerResponse);
        assertEquals(expectedPassengerResponse, actualPassengerResponse);
    }

    @Test
    public void testEditPassenger_WhenPassengerPhoneNumberAlreadyExists_ShouldThrowPhoneNumberUniqueException() {
        Long passengerId = TestPassengerUtil.getFirstPassengerId();
        PassengerRequest passengerRequest = TestPassengerUtil.getPassengerRequest();
        Passenger updatedPassenger = TestPassengerUtil.getFirstPassenger();
        Passenger existingPassenger = TestPassengerUtil.getSecondPassenger();

        when(passengerRepository.findById(passengerId)).thenReturn(Optional.of(updatedPassenger));
        when(passengerRepository.findPassengerByPhoneNumber(passengerRequest.getPhoneNumber())).thenReturn(Optional.of(existingPassenger));

        assertThrows(PhoneNumberUniqueException.class, () -> passengerService.editPassenger(passengerId, passengerRequest));
    }

    @Test
    public void testEditPassenger_WhenPassengerNotFound_ShouldThrowPassengerNotFoundException() {
        Long passengerId = TestPassengerUtil.getFirstPassengerId();
        PassengerRequest passengerRequest = TestPassengerUtil.getPassengerRequest();

        when(passengerRepository.findById(passengerId)).thenReturn(Optional.empty());

        assertThrows(PassengerNotFoundException.class, () -> passengerService.editPassenger(passengerId, passengerRequest));
    }

    @Test
    void testGetPassengerById_WhenPassengerExists_ShouldReturnPassengerResponse() {
        Long passengerId = TestPassengerUtil.getFirstPassengerId();
        Passenger existingPassenger = TestPassengerUtil.getFirstPassenger();
        PassengerResponse expectedPassengerResponse = TestPassengerUtil.getPassengerResponse();
        AveragePassengerRatingResponse passengerRating = TestPassengerUtil.getPassengerRating();

        when(passengerRepository.findById(passengerId)).thenReturn(Optional.of(existingPassenger));
        when(passengerRatingService.getAveragePassengerRating(anyLong())).thenReturn(passengerRating);
        when(modelMapper.map(existingPassenger, PassengerResponse.class)).thenReturn(expectedPassengerResponse);

        PassengerResponse actualPassengerResponse = passengerService.getPassengerById(passengerId);

        assertNotNull(actualPassengerResponse);
        assertEquals(expectedPassengerResponse, actualPassengerResponse);
    }

    @Test
    public void testGetPassengerById_WhenPassengerNotFound_ShouldThrowPassengerNotFoundException() {
        Long passengerId = TestPassengerUtil.getFirstPassengerId();

        when(passengerRepository.findById(passengerId)).thenReturn(Optional.empty());

        assertThrows(PassengerNotFoundException.class, () -> passengerService.getPassengerById(passengerId));
    }

    @Test
    public void testGetAllPassengers_ShouldReturnPassengersPage() {
        int page = TestPassengerUtil.getPageNumber();
        int size = TestPassengerUtil.getPageSize();
        String sortBy = TestPassengerUtil.getSortField();
        AveragePassengerRatingResponse passengerRating = TestPassengerUtil.getPassengerRating();
        Passenger firstPassenger = TestPassengerUtil.getFirstPassenger();
        Passenger secondPassenger = TestPassengerUtil.getSecondPassenger();
        PassengerResponse firstPassengerResponse = TestPassengerUtil.getPassengerResponse();
        PassengerResponse secondPassengerResponse = TestPassengerUtil.getSecondPassengerResponse();

        List<Passenger> mockPassengers = new ArrayList<>();
        mockPassengers.add(firstPassenger);
        mockPassengers.add(secondPassenger);

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<Passenger> mockPassengerPage = new PageImpl<>(mockPassengers, pageable, mockPassengers.size());

        doNothing().when(fieldValidator).checkSortField(eq(Passenger.class), eq(sortBy));
        when(passengerRatingService.getAveragePassengerRating(anyLong())).thenReturn(passengerRating);
        when(modelMapper.map(firstPassenger, PassengerResponse.class)).thenReturn(firstPassengerResponse);
        when(modelMapper.map(secondPassenger, PassengerResponse.class)).thenReturn(secondPassengerResponse);
        when(passengerRepository.findAll(any(Pageable.class))).thenReturn(mockPassengerPage);

        PassengerPageResponse result = passengerService.getAllPassengers(page, size, sortBy);

        assertNotNull(result);
        assertEquals(1, result.getTotalPages());
        assertEquals(size, result.getTotalElements());
        assertEquals(page, result.getCurrentPage());
        assertEquals(size, result.getPassengers().size());
    }

    @Test
    public void testDeletePassengerById_WhenPassengerExists_ShouldMarkPassengerActiveFalseAndCallBankWebClientMethods() {
        Long passengerId = TestPassengerUtil.getFirstPassengerId();
        Passenger passenger = TestPassengerUtil.getFirstPassenger();

        when(passengerRepository.findById(passengerId)).thenReturn(Optional.of(passenger));

        passengerService.deletePassengerById(passengerId);

        assertFalse(passenger.isActive());
        verify(passengerRepository, times(1)).save(passenger);
        verify(bankWebClient, times(1)).deletePassengerBankCards(passengerId);
    }

    @Test
    public void testDeletePassengerById_WhenPassengerNotFound_ShouldThrowPassengerNotFoundException() {
        Long passengerId = TestPassengerUtil.getFirstPassengerId();

        when(passengerRepository.findById(passengerId)).thenReturn(Optional.empty());

        assertThrows(PassengerNotFoundException.class, () -> passengerService.deletePassengerById(passengerId));
    }
}