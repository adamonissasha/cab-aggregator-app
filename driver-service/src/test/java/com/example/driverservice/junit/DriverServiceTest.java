package com.example.driverservice.junit;

import com.example.driverservice.dto.request.DriverRequest;
import com.example.driverservice.dto.response.AverageDriverRatingResponse;
import com.example.driverservice.dto.response.CarResponse;
import com.example.driverservice.dto.response.DriverPageResponse;
import com.example.driverservice.dto.response.DriverResponse;
import com.example.driverservice.exception.CarNotFoundException;
import com.example.driverservice.exception.DriverNotFoundException;
import com.example.driverservice.exception.DriverStatusException;
import com.example.driverservice.exception.PhoneNumberUniqueException;
import com.example.driverservice.kafka.service.KafkaFreeDriverService;
import com.example.driverservice.model.Driver;
import com.example.driverservice.model.enums.Status;
import com.example.driverservice.repository.DriverRepository;
import com.example.driverservice.service.CarService;
import com.example.driverservice.service.DriverRatingService;
import com.example.driverservice.service.impl.DriverServiceImpl;
import com.example.driverservice.util.FieldValidator;
import com.example.driverservice.util.TestCarUtil;
import com.example.driverservice.util.TestDriverUtil;
import com.example.driverservice.webClient.BankWebClient;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DriverServiceTest {
    @Mock
    DriverRepository driverRepository;
    @Mock
    ModelMapper modelMapper;
    @Mock
    FieldValidator fieldValidator;
    @Mock
    KafkaFreeDriverService kafkaFreeDriverService;
    @Mock
    CarService carService;
    @Mock
    DriverRatingService driverRatingService;
    @Mock
    BankWebClient bankWebClient;
    @InjectMocks
    DriverServiceImpl driverService;

    @Test
    public void testCreateDriver_WhenPhoneNumberUniqueAndCarExists_ShouldCreateDriver() {
        DriverRequest driverRequest = TestDriverUtil.getDriverRequest();
        CarResponse carResponse = TestCarUtil.getFirstCarResponse();
        Driver newDriver = TestDriverUtil.getFirstDriver();
        DriverResponse expected = TestDriverUtil.getDriverResponse();
        AverageDriverRatingResponse driverRating = TestDriverUtil.getDriverRating();

        when(carService.getCarById(driverRequest.getCarId()))
                .thenReturn(carResponse);
        when(driverRepository.findDriverByPhoneNumber(driverRequest.getPhoneNumber())).
                thenReturn(Optional.empty());
        when(modelMapper.map(any(DriverRequest.class), eq(Driver.class)))
                .thenReturn(newDriver);
        when(driverRepository.save(any(Driver.class)))
                .thenReturn(TestDriverUtil.getFirstDriver());
        when(driverRatingService.getAverageDriverRating(anyLong()))
                .thenReturn(driverRating);
        when(modelMapper.map(any(Driver.class), eq(DriverResponse.class)))
                .thenReturn(expected);
        doNothing()
                .when(kafkaFreeDriverService)
                .sendFreeDriverToConsumer(any(DriverResponse.class));

        DriverResponse actual = driverService.createDriver(driverRequest);

        assertEquals(expected, actual);

        verify(carService, times(2))
                .getCarById(driverRequest.getCarId());
        verify(driverRepository, times(1))
                .findDriverByPhoneNumber(driverRequest.getPhoneNumber());
        verify(driverRepository, times(1))
                .save(any(Driver.class));
        verify(driverRatingService, times(1))
                .getAverageDriverRating(anyLong());
        verify(kafkaFreeDriverService, times(1))
                .sendFreeDriverToConsumer(expected);
    }

    @Test
    public void testCreateDriver_WhenPhoneNumberAlreadyExists_ShouldThrowPhoneNumberUniqueException() {
        DriverRequest driverRequest = TestDriverUtil.getDriverRequest();
        Driver existingDriver = TestDriverUtil.getFirstDriver();
        String existingDriverPhoneNumber = TestDriverUtil.getFirstDriverPhoneNumber();

        when(driverRepository.findDriverByPhoneNumber(existingDriverPhoneNumber))
                .thenReturn(Optional.of(existingDriver));

        assertThrows(PhoneNumberUniqueException.class, () -> driverService.createDriver(driverRequest));

        verify(driverRepository, times(1))
                .findDriverByPhoneNumber(existingDriverPhoneNumber);
    }

    @Test
    public void testCreateDriver_WhenCarWithIdNotFound_ShouldThrowCarNotFoundException() {
        DriverRequest driverRequest = TestDriverUtil.getDriverRequest();

        when(carService.getCarById(anyLong()))
                .thenThrow(new CarNotFoundException(String.format(TestDriverUtil.getCarNotFoundMessage(), anyLong())));

        assertThrows(CarNotFoundException.class, () -> driverService.createDriver(driverRequest));

        verify(carService, times(1))
                .getCarById(anyLong());
    }

    @Test
    public void testEditDriver_WhenDriverExistsAndDriverPhoneNumberUnique_ShouldEditDriver() {
        Long driverId = TestDriverUtil.getFirstDriverId();
        DriverRequest driverRequest = TestDriverUtil.getDriverRequest();
        Driver updatedDriver = TestDriverUtil.getFirstDriver();
        Driver existingDriver = TestDriverUtil.getSecondDriver();
        DriverResponse expected = TestDriverUtil.getDriverResponse();
        AverageDriverRatingResponse driverRating = TestDriverUtil.getDriverRating();

        when(driverRepository.findById(driverId))
                .thenReturn(Optional.of(existingDriver));
        when(driverRepository.findDriverByPhoneNumber(driverRequest.getPhoneNumber()))
                .thenReturn(Optional.empty());
        when(modelMapper.map(driverRequest, Driver.class))
                .thenReturn(updatedDriver);
        when(driverRepository.save(updatedDriver))
                .thenReturn(updatedDriver);
        when(driverRatingService.getAverageDriverRating(anyLong()))
                .thenReturn(driverRating);
        when(modelMapper.map(updatedDriver, DriverResponse.class))
                .thenReturn(expected);

        DriverResponse actual = driverService.editDriver(driverId, driverRequest);

        assertEquals(expected, actual);

        verify(driverRepository, times(1))
                .findById(driverId);
        verify(driverRepository, times(1))
                .findDriverByPhoneNumber(driverRequest.getPhoneNumber());
        verify(driverRepository, times(1))
                .save(updatedDriver);
        verify(driverRatingService, times(1))
                .getAverageDriverRating(anyLong());
    }

    @Test
    public void testEditDriver_WhenDriverPhoneNumberAlreadyExists_ShouldThrowPhoneNumberUniqueException() {
        Long driverId = TestDriverUtil.getFirstDriverId();
        DriverRequest driverRequest = TestDriverUtil.getDriverRequest();
        Driver updatedDriver = TestDriverUtil.getFirstDriver();
        Driver existingDriver = TestDriverUtil.getSecondDriver();

        when(driverRepository.findById(driverId))
                .thenReturn(Optional.of(updatedDriver));
        when(driverRepository.findDriverByPhoneNumber(driverRequest.getPhoneNumber()))
                .thenReturn(Optional.of(existingDriver));

        assertThrows(PhoneNumberUniqueException.class, () -> driverService.editDriver(driverId, driverRequest));

        verify(driverRepository, times(1))
                .findById(driverId);
        verify(driverRepository, times(1))
                .findDriverByPhoneNumber(driverRequest.getPhoneNumber());
    }

    @Test
    public void testEditDriver_WhenDriverNotFound_ShouldThrowDriverNotFoundException() {
        Long driverId = TestDriverUtil.getFirstDriverId();
        DriverRequest driverRequest = TestDriverUtil.getDriverRequest();

        when(driverRepository.findById(driverId))
                .thenReturn(Optional.empty());

        assertThrows(DriverNotFoundException.class, () -> driverService.editDriver(driverId, driverRequest));

        verify(driverRepository, times(1))
                .findById(driverId);
    }

    @Test
    void testGetDriverById_WhenDriverExists_ShouldReturnDriverResponse() {
        Long driverId = TestDriverUtil.getFirstDriverId();
        Driver existingDriver = TestDriverUtil.getFirstDriver();
        DriverResponse expected = TestDriverUtil.getDriverResponse();
        AverageDriverRatingResponse driverRating = TestDriverUtil.getDriverRating();

        when(driverRepository.findById(driverId))
                .thenReturn(Optional.of(existingDriver));
        when(driverRatingService.getAverageDriverRating(anyLong()))
                .thenReturn(driverRating);
        when(modelMapper.map(existingDriver, DriverResponse.class))
                .thenReturn(expected);

        DriverResponse actual = driverService.getDriverById(driverId);

        assertEquals(expected, actual);

        verify(driverRepository, times(1))
                .findById(driverId);
        verify(driverRatingService, times(1))
                .getAverageDriverRating(anyLong());
    }

    @Test
    public void testGetDriverById_WhenDriverNotFound_ShouldThrowDriverNotFoundException() {
        Long driverId = TestDriverUtil.getFirstDriverId();

        when(driverRepository.findById(driverId))
                .thenReturn(Optional.empty());

        assertThrows(DriverNotFoundException.class, () -> driverService.getDriverById(driverId));

        verify(driverRepository, times(1))
                .findById(driverId);
    }

    @Test
    public void testGetAllDrivers_ShouldReturnDriversPage() {
        int page = TestDriverUtil.getPageNumber();
        int size = TestDriverUtil.getPageSize();
        String sortBy = TestDriverUtil.getSortField();
        AverageDriverRatingResponse driverRating = TestDriverUtil.getDriverRating();
        List<Driver> drivers = TestDriverUtil.getDrivers();
        List<DriverResponse> driverResponses = TestDriverUtil.getDriverResponses();

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<Driver> mockDriverPage = new PageImpl<>(drivers, pageable, drivers.size());
        DriverPageResponse expected = DriverPageResponse.builder()
                .drivers(driverResponses)
                .currentPage(page)
                .pageSize(size)
                .totalElements(6)
                .totalPages(3)
                .build();

        doNothing()
                .when(fieldValidator)
                .checkSortField(eq(Driver.class), eq(sortBy));
        when(driverRatingService.getAverageDriverRating(anyLong()))
                .thenReturn(driverRating);
        when(modelMapper.map(drivers.get(0), DriverResponse.class))
                .thenReturn(driverResponses.get(0));
        when(modelMapper.map(drivers.get(1), DriverResponse.class))
                .thenReturn(driverResponses.get(1));
        when(driverRepository.findAll(any(Pageable.class)))
                .thenReturn(mockDriverPage);

        DriverPageResponse actual = driverService.getAllDrivers(page, size, sortBy);

        assertEquals(expected, actual);

        verify(driverRatingService, times(2))
                .getAverageDriverRating(anyLong());
        verify(driverRepository, times(1))
                .findAll(any(Pageable.class));
    }

    @Test
    public void testDeleteDriverById_WhenDriverExists_ShouldMarkDriverActiveFalseAndCallBankWebClientMethods() {
        Long driverId = TestDriverUtil.getFirstDriverId();
        Driver driver = TestDriverUtil.getFirstDriver();

        when(driverRepository.findById(driverId))
                .thenReturn(Optional.of(driver));

        driverService.deleteDriverById(driverId);

        assertFalse(driver.isActive());

        verify(driverRepository, times(1))
                .save(driver);
        verify(bankWebClient, times(1))
                .deleteDriverBankAccount(driverId);
        verify(bankWebClient, times(1))
                .deleteDriverBankCards(driverId);
    }

    @Test
    public void testDeleteDriverById_WhenDriverNotFound_ShouldThrowDriverNotFoundException() {
        Long driverId = TestDriverUtil.getFirstDriverId();

        when(driverRepository.findById(driverId))
                .thenReturn(Optional.empty());

        assertThrows(DriverNotFoundException.class, () -> driverService.deleteDriverById(driverId));

        verify(driverRepository, times(1))
                .findById(driverId);
    }

    @Test
    public void testChangeDriverStatusToFree_WhenDriverExistsAndStatusBusy_ShouldChangeDriverStatus() {
        Long driverId = TestDriverUtil.getFirstDriverId();
        Driver existingDriver = TestDriverUtil.getFirstDriver();
        existingDriver.setStatus(Status.BUSY);
        AverageDriverRatingResponse driverRating = TestDriverUtil.getDriverRating();
        DriverResponse driverResponse = TestDriverUtil.getDriverResponse();

        when(driverRepository.findById(driverId))
                .thenReturn(Optional.of(existingDriver));
        when(driverRepository.save(existingDriver))
                .thenReturn(existingDriver);
        when(driverRatingService.getAverageDriverRating(anyLong()))
                .thenReturn(driverRating);
        when(modelMapper.map(existingDriver, DriverResponse.class))
                .thenReturn(driverResponse);

        driverResponse = driverService.changeDriverStatusToFree(driverId);

        assertEquals(Status.FREE, existingDriver.getStatus());

        verify(driverRepository, times(1))
                .findById(driverId);
        verify(driverRatingService, times(1))
                .getAverageDriverRating(anyLong());
        verify(driverRepository, times(1))
                .save(existingDriver);
        verify(kafkaFreeDriverService, times(1))
                .sendFreeDriverToConsumer(driverResponse);
    }

    @Test
    public void testChangeDriverStatusToFree_WhenDriverExistsAndStatusIsAlreadyFree_ShouldThrowDriverStatusException() {
        Long driverId = TestDriverUtil.getFirstDriverId();
        Driver existingDriver = TestDriverUtil.getFirstDriver();

        when(driverRepository.findById(driverId))
                .thenReturn(Optional.of(existingDriver));

        assertThrows(DriverStatusException.class, () -> driverService.changeDriverStatusToFree(driverId));

        verify(driverRepository, times(1))
                .findById(driverId);
        verify(driverRepository, never())
                .save(existingDriver);
        verify(kafkaFreeDriverService, never())
                .sendFreeDriverToConsumer(any(DriverResponse.class));
    }

    @Test
    public void testChangeDriverStatusToFree_WhenDriverNotFound_ShouldThrowDriverNotFoundException() {
        Long driverId = TestDriverUtil.getFirstDriverId();

        when(driverRepository.findById(driverId))
                .thenReturn(Optional.empty());

        assertThrows(DriverNotFoundException.class, () -> driverService.changeDriverStatusToFree(driverId));

        verify(driverRepository, times(1))
                .findById(driverId);
        verify(driverRepository, never())
                .save(any(Driver.class));
        verify(kafkaFreeDriverService, never())
                .sendFreeDriverToConsumer(any(DriverResponse.class));
    }
}