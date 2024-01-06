package com.example.driverservice.service;

import com.example.driverservice.dto.request.CarRequest;
import com.example.driverservice.dto.response.CarPageResponse;
import com.example.driverservice.dto.response.CarResponse;
import com.example.driverservice.exception.CarNotFoundException;
import com.example.driverservice.exception.CarNumberUniqueException;
import com.example.driverservice.model.Car;
import com.example.driverservice.repository.CarRepository;
import com.example.driverservice.service.impl.CarServiceImpl;
import com.example.driverservice.util.FieldValidator;
import com.example.driverservice.util.TestCarUtil;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CarServiceTest {
    @Mock
    CarRepository carRepository;
    @Mock
    ModelMapper modelMapper;
    @Mock
    FieldValidator fieldValidator;
    @InjectMocks
    CarServiceImpl carService;

    @Test
    public void testCreateCar_WhenCarNumberUnique_ShouldCreateCar() {
        CarRequest carRequest = TestCarUtil.getCarRequest();
        Car newCar = TestCarUtil.getFirstCar();
        CarResponse expected = TestCarUtil.getFirstCarResponse();

        when(carRepository.findCarByNumber(carRequest.getNumber()))
                .thenReturn(Optional.empty());
        when(modelMapper.map(carRequest, Car.class))
                .thenReturn(newCar);
        when(carRepository.save(any(Car.class)))
                .thenReturn(newCar);
        when(modelMapper.map(newCar, CarResponse.class))
                .thenReturn(expected);

        CarResponse actual = carService.createCar(carRequest);

        assertEquals(expected, actual);

        verify(carRepository, times(1))
                .findCarByNumber(carRequest.getNumber());
        verify(carRepository, times(1))
                .save(any(Car.class));
    }

    @Test
    public void testCreateCar_WhenCarNumberAlreadyExists_ShouldThrowCarNumberUniqueException() {
        CarRequest carRequest = TestCarUtil.getCarRequest();
        Car existingCar = TestCarUtil.getFirstCar();
        String existingCarNumber = TestCarUtil.getFirstCarNumber();

        when(carRepository.findCarByNumber(existingCarNumber))
                .thenReturn(Optional.of(existingCar));

        assertThrows(CarNumberUniqueException.class, () -> carService.createCar(carRequest));

        verify(carRepository, times(1))
                .findCarByNumber(existingCarNumber);
    }

    @Test
    public void testEditCar_WhenCarExistsAndCarNumberUnique_ShouldEditCar() {
        Long carId = TestCarUtil.getFirstCarId();
        CarRequest carRequest = TestCarUtil.getCarRequest();
        Car existingCar = TestCarUtil.getFirstCar();
        Car updatedCar = TestCarUtil.getSecondCar();
        CarResponse expected = TestCarUtil.getFirstCarResponse();

        when(carRepository.findById(carId))
                .thenReturn(Optional.of(existingCar));
        when(carRepository.findCarByNumber(carRequest.getNumber()))
                .thenReturn(Optional.empty());
        when(modelMapper.map(carRequest, Car.class))
                .thenReturn(updatedCar);
        when(carRepository.save(updatedCar))
                .thenReturn(updatedCar);
        when(modelMapper.map(updatedCar, CarResponse.class))
                .thenReturn(expected);

        CarResponse actual = carService.editCar(carId, carRequest);

        assertEquals(expected, actual);

        verify(carRepository, times(1))
                .findById(carId);
        verify(carRepository, times(1))
                .findCarByNumber(carRequest.getNumber());
        verify(carRepository, times(1))
                .save(updatedCar);
    }

    @Test
    public void testEditCar_WhenCarNumberAlreadyExists_ShouldThrowCarNumberUniqueException() {
        Long carId = TestCarUtil.getFirstCarId();
        CarRequest carRequest = TestCarUtil.getCarRequest();
        Car updatedCar = TestCarUtil.getFirstCar();
        Car existingCar = TestCarUtil.getSecondCar();

        when(carRepository.findById(carId))
                .thenReturn(Optional.of(updatedCar));
        when(carRepository.findCarByNumber(carRequest.getNumber()))
                .thenReturn(Optional.of(existingCar));

        assertThrows(CarNumberUniqueException.class, () -> carService.editCar(carId, carRequest));

        verify(carRepository, times(1))
                .findById(carId);
        verify(carRepository, times(1))
                .findCarByNumber(carRequest.getNumber());
    }

    @Test
    public void testEditCar_WhenCarNotFound_ShouldThrowCarNotFoundException() {
        Long carId = TestCarUtil.getFirstCarId();
        CarRequest carRequest = TestCarUtil.getCarRequest();

        when(carRepository.findById(carId))
                .thenReturn(Optional.empty());

        assertThrows(CarNotFoundException.class, () -> carService.editCar(carId, carRequest));

        verify(carRepository, times(1))
                .findById(carId);
    }

    @Test
    void testGetCarById_WhenCarExists_ShouldReturnCarResponse() {
        Long carId = TestCarUtil.getFirstCarId();
        Car car = TestCarUtil.getFirstCar();
        CarResponse expected = TestCarUtil.getFirstCarResponse();

        when(carRepository.findById(carId))
                .thenReturn(Optional.of(car));
        when(modelMapper.map(car, CarResponse.class))
                .thenReturn(expected);

        CarResponse actual = carService.getCarById(carId);

        assertEquals(expected, actual);

        verify(carRepository, times(1))
                .findById(carId);
    }

    @Test
    public void testGetCarById_WhenCarNotFound_ShouldThrowCarNotFoundException() {
        Long carId = TestCarUtil.getFirstCarId();

        when(carRepository.findById(carId))
                .thenReturn(Optional.empty());

        assertThrows(CarNotFoundException.class, () -> carService.getCarById(carId));

        verify(carRepository, times(1))
                .findById(carId);
    }

    @Test
    public void testGetAllCars_ShouldReturnCarsPage() {
        int page = TestCarUtil.getPageNumber();
        int size = TestCarUtil.getPageSize();
        String sortBy = TestCarUtil.getSortField();
        List<Car> cars = TestCarUtil.getCars();
        List<CarResponse> carResponses = TestCarUtil.getCarResponses();

        CarPageResponse expected = CarPageResponse.builder()
                .cars(carResponses)
                .currentPage(page)
                .pageSize(size)
                .totalElements(2)
                .totalPages(1)
                .build();

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<Car> mockCarPage = new PageImpl<>(cars, pageable, cars.size());

        doNothing()
                .when(fieldValidator)
                .checkSortField(eq(Car.class), eq(sortBy));
        when(modelMapper.map(cars.get(0), CarResponse.class))
                .thenReturn(carResponses.get(0));
        when(modelMapper.map(cars.get(1), CarResponse.class))
                .thenReturn(carResponses.get(1));
        when(carRepository.findAll(any(Pageable.class)))
                .thenReturn(mockCarPage);

        CarPageResponse actual = carService.getAllCars(page, size, sortBy);

        assertEquals(expected, actual);

        verify(carRepository, times(1))
                .findAll(any(Pageable.class));
    }
}