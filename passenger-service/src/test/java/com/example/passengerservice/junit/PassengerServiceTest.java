package com.example.passengerservice.junit;

import com.example.passengerservice.dto.request.PassengerRequest;
import com.example.passengerservice.dto.response.AveragePassengerRatingResponse;
import com.example.passengerservice.dto.response.PassengerPageResponse;
import com.example.passengerservice.dto.response.PassengerResponse;
import com.example.passengerservice.exception.IncorrectFieldNameException;
import com.example.passengerservice.exception.PassengerNotFoundException;
import com.example.passengerservice.exception.PhoneNumberUniqueException;
import com.example.passengerservice.model.Passenger;
import com.example.passengerservice.repository.PassengerRepository;
import com.example.passengerservice.service.PassengerRatingService;
import com.example.passengerservice.service.impl.PassengerServiceImpl;
import com.example.passengerservice.util.FieldValidator;
import com.example.passengerservice.util.TestPassengerUtil;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
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
    @InjectMocks
    PassengerServiceImpl passengerService;

    @Test
    public void testCreatePassenger_WhenPhoneNumberUnique_ShouldCreatePassenger() {
        PassengerRequest passengerRequest = TestPassengerUtil.getPassengerRequest();
        Passenger newPassenger = TestPassengerUtil.getFirstPassenger();
        PassengerResponse expected = TestPassengerUtil.getPassengerResponse();
        AveragePassengerRatingResponse passengerRating = TestPassengerUtil.getFirstPassengerRating();

        when(passengerRepository.findPassengerByPhoneNumber(passengerRequest.getPhoneNumber()))
                .thenReturn(Mono.empty());
        when(modelMapper.map(any(PassengerRequest.class), eq(Passenger.class)))
                .thenReturn(newPassenger);
        when(passengerRepository.save(any(Passenger.class)))
                .thenReturn(Mono.just(newPassenger));
        when(passengerRatingService.getAveragePassengerRating(anyString()))
                .thenReturn(Mono.just(passengerRating));
        when(modelMapper.map(any(Passenger.class), eq(PassengerResponse.class)))
                .thenReturn(expected);

        StepVerifier.create(passengerService.createPassenger(passengerRequest))
                .expectNext(expected)
                .verifyComplete();

        verify(passengerRepository, times(1))
                .save(any(Passenger.class));
        verify(passengerRepository, times(1))
                .findPassengerByPhoneNumber(anyString());
        verify(passengerRatingService, times(1))
                .getAveragePassengerRating(anyString());
    }

    @Test
    public void testCreatePassenger_WhenPhoneNumberAlreadyExists_ShouldThrowPhoneNumberUniqueException() {
        PassengerRequest passengerRequest = TestPassengerUtil.getPassengerRequest();
        Passenger existingPassenger = TestPassengerUtil.getFirstPassenger();
        String existingPassengerPhoneNumber = TestPassengerUtil.getFirstPassengerPhoneNumber();

        when(passengerRepository.findPassengerByPhoneNumber(existingPassengerPhoneNumber))
                .thenReturn(Mono.just(existingPassenger));

        StepVerifier.create(passengerService.createPassenger(passengerRequest))
                .expectError(PhoneNumberUniqueException.class)
                .verify();

        verify(passengerRepository, times(1))
                .findPassengerByPhoneNumber(passengerRequest.getPhoneNumber());
    }

    @Test
    public void testEditPassenger_WhenPassengerExistsAndPassengerPhoneNumberUnique_ShouldEditPassenger() {
        String passengerId = TestPassengerUtil.getFirstPassengerId();
        PassengerRequest passengerRequest = TestPassengerUtil.getPassengerRequest();
        Passenger updatedPassenger = TestPassengerUtil.getFirstPassenger();
        Passenger existingPassenger = TestPassengerUtil.getSecondPassenger();
        PassengerResponse expected = TestPassengerUtil.getPassengerResponse();
        AveragePassengerRatingResponse passengerRating = TestPassengerUtil.getFirstPassengerRating();

        when(passengerRepository.findById(passengerId))
                .thenReturn(Mono.just(existingPassenger));
        when(passengerRepository.findPassengerByPhoneNumber(passengerRequest.getPhoneNumber()))
                .thenReturn(Mono.empty());
        when(modelMapper.map(passengerRequest, Passenger.class))
                .thenReturn(updatedPassenger);
        when(passengerRepository.save(updatedPassenger))
                .thenReturn(Mono.just(updatedPassenger));
        when(passengerRatingService.getAveragePassengerRating(anyString()))
                .thenReturn(Mono.just(passengerRating));
        when(modelMapper.map(updatedPassenger, PassengerResponse.class))
                .thenReturn(expected);

        StepVerifier.create(passengerService.editPassenger(passengerId, passengerRequest))
                .expectNext(expected)
                .verifyComplete();

        verify(passengerRepository, times(1))
                .findById(passengerId);
        verify(passengerRepository, times(1))
                .findPassengerByPhoneNumber(passengerRequest.getPhoneNumber());
        verify(passengerRepository, times(1))
                .save(updatedPassenger);
        verify(passengerRatingService, times(1))
                .getAveragePassengerRating(anyString());
    }

    @Test
    public void testEditPassenger_WhenPassengerPhoneNumberAlreadyExists_ShouldThrowPhoneNumberUniqueException() {
        String passengerId = TestPassengerUtil.getFirstPassengerId();
        Passenger updatedPassenger = TestPassengerUtil.getFirstPassenger();
        Passenger existingPassenger = TestPassengerUtil.getSecondPassenger();
        PassengerRequest passengerRequest = TestPassengerUtil.getPassengerRequest();
        passengerRequest.setPhoneNumber(existingPassenger.getPhoneNumber());

        when(passengerRepository.findById(passengerId))
                .thenReturn(Mono.just(updatedPassenger));
        when(passengerRepository.findPassengerByPhoneNumber(passengerRequest.getPhoneNumber()))
                .thenReturn(Mono.just(existingPassenger));

        StepVerifier.create(passengerService.editPassenger(passengerId, passengerRequest))
                .expectError(PhoneNumberUniqueException.class)
                .verify();

        verify(passengerRepository, times(1))
                .findById(passengerId);
        verify(passengerRepository, times(1))
                .findPassengerByPhoneNumber(passengerRequest.getPhoneNumber());
    }

    @Test
    public void testEditPassenger_WhenPassengerNotFound_ShouldThrowPassengerNotFoundException() {
        String passengerId = TestPassengerUtil.getFirstPassengerId();
        PassengerRequest passengerRequest = TestPassengerUtil.getPassengerRequest();

        when(passengerRepository.findById(passengerId))
                .thenReturn(Mono.empty());

        StepVerifier.create(passengerService.editPassenger(passengerId, passengerRequest))
                .expectError(PassengerNotFoundException.class)
                .verify();

        verify(passengerRepository, times(1))
                .findById(passengerId);
    }

    @Test
    void testGetPassengerById_WhenPassengerExists_ShouldReturnPassengerResponse() {
        String passengerId = TestPassengerUtil.getFirstPassengerId();
        Passenger existingPassenger = TestPassengerUtil.getFirstPassenger();
        PassengerResponse expected = TestPassengerUtil.getPassengerResponse();
        AveragePassengerRatingResponse passengerRating = TestPassengerUtil.getFirstPassengerRating();

        when(passengerRepository.findById(passengerId))
                .thenReturn(Mono.just(existingPassenger));
        when(passengerRatingService.getAveragePassengerRating(anyString()))
                .thenReturn(Mono.just(passengerRating));
        when(modelMapper.map(existingPassenger, PassengerResponse.class))
                .thenReturn(expected);

        StepVerifier.create(passengerService.getPassengerById(passengerId))
                .expectNext(expected)
                .verifyComplete();

        verify(passengerRepository, times(1))
                .findById(passengerId);
        verify(passengerRatingService, times(1))
                .getAveragePassengerRating(anyString());
    }

    @Test
    public void testGetPassengerById_WhenPassengerNotFound_ShouldThrowPassengerNotFoundException() {
        String passengerId = TestPassengerUtil.getFirstPassengerId();

        when(passengerRepository.findById(passengerId))
                .thenReturn(Mono.empty());

        StepVerifier.create(passengerService.getPassengerById(passengerId))
                .expectError(PassengerNotFoundException.class)
                .verify();

        verify(passengerRepository, times(1))
                .findById(passengerId);
    }

    @Test
    public void testGetAllPassengers_ShouldReturnPassengersPage() {
        int page = TestPassengerUtil.getPageNumber();
        int size = TestPassengerUtil.getPageSize();
        String sortBy = TestPassengerUtil.getCorrectSortField();
        AveragePassengerRatingResponse passengerRating = TestPassengerUtil.getFirstPassengerRating();
        List<Passenger> passengers = TestPassengerUtil.getPassengers();
        List<PassengerResponse> passengerResponses = TestPassengerUtil.getPassengerResponses();

        Flux<Passenger> mockPassengerFlux = Flux.fromIterable(passengers);
        PassengerPageResponse expected = PassengerPageResponse.builder()
                .passengers(passengerResponses)
                .currentPage(page)
                .pageSize(size)
                .totalElements(2)
                .totalPages(1)
                .build();

        when(fieldValidator.checkSortField(eq(Passenger.class), eq(sortBy)))
                .thenReturn(Mono.empty());
        when(passengerRepository.countAllByIsActiveTrue())
                .thenReturn(Mono.just(2L));
        when(passengerRatingService.getAveragePassengerRating(anyString()))
                .thenReturn(Mono.just(passengerRating));
        when(modelMapper.map(passengers.get(0), PassengerResponse.class))
                .thenReturn(passengerResponses.get(0));
        when(modelMapper.map(passengers.get(1), PassengerResponse.class))
                .thenReturn(passengerResponses.get(1));
        when(passengerRepository.findAllByIsActiveTrue(any(Pageable.class)))
                .thenReturn(mockPassengerFlux);

        StepVerifier.create(passengerService.getAllPassengers(page, size, sortBy))
                .expectNext(expected)
                .verifyComplete();

        verify(passengerRepository, times(1))
                .findAllByIsActiveTrue(any(Pageable.class));
        verify(passengerRatingService, times(2))
                .getAveragePassengerRating(anyString());
    }

    @Test
    public void testGetAllPassengers_WhenIncorrectField_ShouldThrowIncorrectFieldException() {
        int page = TestPassengerUtil.getPageNumber();
        int size = TestPassengerUtil.getPageSize();
        String sortBy = TestPassengerUtil.getIncorrectSortField();

        when(fieldValidator.checkSortField(eq(Passenger.class), eq(sortBy)))
                .thenReturn(Mono.error(new IncorrectFieldNameException("Invalid sortBy field. Allowed fields: [id, firstName, lastName, email, phoneNumber, password, isActive]")));

        StepVerifier.create(passengerService.getAllPassengers(page, size, sortBy))
                .expectError(IncorrectFieldNameException.class)
                .verify();

        verify(fieldValidator, times(1))
                .checkSortField(eq(Passenger.class), eq(sortBy));
        verify(passengerRepository, never())
                .findAllByIsActiveTrue(any(Pageable.class));
    }

    @Test
    public void testDeletePassengerById_WhenPassengerExists_ShouldMarkPassengerActiveFalseAndCallBankWebClientMethods() {
        String passengerId = TestPassengerUtil.getFirstPassengerId();
        Passenger passenger = TestPassengerUtil.getFirstPassenger();
        Passenger deletingPassenger = TestPassengerUtil.getFirstPassenger();
        deletingPassenger.setActive(false);

        when(passengerRepository.findById(passengerId))
                .thenReturn(Mono.just(passenger));
        when(passengerRepository.save(passenger))
                .thenReturn(Mono.empty());

        StepVerifier.create(passengerService.deletePassengerById(passengerId))
                .verifyComplete();

        assertFalse(deletingPassenger.isActive());

        verify(passengerRepository, times(1))
                .save(passenger);
        verify(passengerRepository, times(1))
                .findById(passengerId);
    }

    @Test
    public void testDeletePassengerById_WhenPassengerNotFound_ShouldThrowPassengerNotFoundException() {
        String passengerId = TestPassengerUtil.getFirstPassengerId();

        when(passengerRepository.findById(passengerId))
                .thenReturn(Mono.empty());

        StepVerifier.create(passengerService.deletePassengerById(passengerId))
                .expectError(PassengerNotFoundException.class)
                .verify();

        verify(passengerRepository, times(1))
                .findById(passengerId);
    }
}