package com.example.ridesservice.component.stepDefinitions;


import com.example.ridesservice.dto.request.PromoCodeRequest;
import com.example.ridesservice.dto.response.AllPromoCodesResponse;
import com.example.ridesservice.dto.response.PromoCodeResponse;
import com.example.ridesservice.exception.IncorrectDateException;
import com.example.ridesservice.exception.PromoCodeAlreadyExistsException;
import com.example.ridesservice.exception.PromoCodeNotFoundException;
import com.example.ridesservice.model.PromoCode;
import com.example.ridesservice.repository.PromoCodeRepository;
import com.example.ridesservice.service.impl.PromoCodeServiceImpl;
import com.example.ridesservice.util.FieldValidator;
import com.example.ridesservice.util.TestPromoCodeUtil;
import com.example.ridesservice.webClient.BankWebClient;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class PromoCodeServiceStepDefinitions {
    @Mock
    private PromoCodeRepository promoCodeRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    BankWebClient bankWebClient;

    @Mock
    FieldValidator fieldValidator;

    @InjectMocks
    private PromoCodeServiceImpl promoCodeService;

    private PromoCodeRequest promoCodeRequest;
    private PromoCode promoCode;
    private PromoCodeResponse expected;
    private PromoCode expectedPromoCode;
    private PromoCode actualPromoCode;
    private PromoCodeResponse actual;
    private Exception exception;
    private AllPromoCodesResponse expectedAllPromoCodesResponse;
    private AllPromoCodesResponse actualAllPromoCodesResponse;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Given("There is a promo code with id {long}")
    public void thereIsAPromoCodeWithId(long id) {
        promoCode = TestPromoCodeUtil.getFirstPromoCode();
        expected = TestPromoCodeUtil.getFirstPromoCodeResponse();

        when(promoCodeRepository.findById(id))
                .thenReturn(Optional.of(promoCode));
        when(modelMapper.map(promoCode, PromoCodeResponse.class))
                .thenReturn(expected);

        assertTrue(promoCodeRepository.findById(id).isPresent());
    }

    @Given("There is no promo code with id {long}")
    public void thereIsNoPromoCodeWithId(long id) {
        promoCode = TestPromoCodeUtil.getFirstPromoCode();
        promoCode.setId(id);

        when(promoCodeRepository.findById(id))
                .thenReturn(Optional.empty());

        Optional<PromoCode> promoCode = promoCodeRepository.findById(id);
        assertFalse(promoCode.isPresent());
    }

    @When("Method getPromoCodeById called with id {long}")
    public void methodGetPromoCodeByIdCalledWithId(long id) {
        try {
            actual = promoCodeService.getPromoCodeById(id);
        } catch (PromoCodeNotFoundException ex) {
            exception = ex;
        }
    }

    @Then("The response should contain the details of promo code with id {long}")
    public void theResponseShouldContainTheDetailsOfPromoCodeWithId(long id) {
        promoCode = promoCodeRepository.findById(id).get();
        expected = promoCodeService.mapPromoCodeToPromoCodeResponse(promoCode);

        assertThat(actual).isEqualTo(expected);
    }

    @Then("The PromoCodeNotFoundException should be thrown with message {string}")
    public void thePromoCodeNotFoundExceptionShouldBeThrownWithMessage(String message) {
        assertEquals(message, exception.getMessage());
    }

    @Given("New promo code {string} unique and dates valid")
    public void newPromoCodeUniqueAndDatesValid(String code) {
        promoCodeRequest = TestPromoCodeUtil.getPromoCodeRequest();
        promoCodeRequest.setCode(code);
        promoCode = TestPromoCodeUtil.getFirstPromoCode();
        expected = TestPromoCodeUtil.getFirstPromoCodeResponse();

        when(modelMapper.map(promoCodeRequest, PromoCode.class))
                .thenReturn(promoCode);
        when(promoCodeRepository.findByCode(code))
                .thenReturn(Optional.empty());
        when(promoCodeRepository.save(any(PromoCode.class)))
                .thenReturn(promoCode);
        when(modelMapper.map(promoCode, PromoCodeResponse.class))
                .thenReturn(expected);

        assertFalse(promoCodeRepository.findByCode(code).isPresent());
    }

    @Given("New promo code has invalid dates: start date {string}, end date {string}")
    public void newPromoCodeHasInvalidDates(String startDate, String endDate) {
        promoCodeRequest = TestPromoCodeUtil.getPromoCodeRequest();
        promoCodeRequest.setStartDate(LocalDate.parse(startDate));
        promoCodeRequest.setEndDate(LocalDate.parse(endDate));

        assertTrue(LocalDate.parse(startDate).isAfter(LocalDate.parse(endDate)));
    }

    @Given("New promo code {string} already exists with later end date")
    public void newPromoCodeAlreadyExistsWithLaterEndDate(String code) {
        promoCodeRequest = TestPromoCodeUtil.getPromoCodeRequest();
        promoCodeRequest.setCode(code);
        promoCode = TestPromoCodeUtil.getSecondPromoCode();

        when(promoCodeRepository.findByCode(code))
                .thenReturn(Optional.of(promoCode));

        assertTrue(promoCodeRepository.findByCode(code)
                .isPresent());
    }

    @When("Method createPromoCode called")
    public void methodCreatePromoCodeCalled() {
        try {
            actual = promoCodeService.createPromoCode(promoCodeRequest);
        } catch (IncorrectDateException | PromoCodeAlreadyExistsException ex) {
            exception = ex;
        }
    }

    @Then("The response should contain the details of the created promo code")
    public void theResponseShouldContainTheDetailsOfTheCreatedPromoCode() {
        promoCode = promoCodeRepository.save(promoCode);
        expected = promoCodeService.mapPromoCodeToPromoCodeResponse(promoCode);

        assertEquals(expected, actual);
    }

    @Then("The IncorrectDateException should be thrown with message {string}")
    public void theIncorrectDateExceptionShouldBeThrownWithMessage(String message) {
        assertEquals(message, exception.getMessage());
    }

    @Then("The PromoCodeAlreadyExistsException should be thrown with message {string}")
    public void thePromoCodeAlreadyExistsExceptionShouldBeThrownWithMessage(String message) {
        assertEquals(message, exception.getMessage());
    }

    @Given("There is a promo code with code {string}")
    public void thereIsAPromoCodeWithCode(String code) {
        expectedPromoCode = TestPromoCodeUtil.getSecondPromoCode();
        expectedPromoCode.setStartDate(LocalDate.now().minusDays(5));
        expectedPromoCode.setEndDate(LocalDate.now().plusDays(5));

        when(promoCodeRepository.findByCode(code))
                .thenReturn(Optional.of(expectedPromoCode));

        assertTrue(promoCodeRepository.findByCode(code).isPresent());
    }

    @Given("There is no promo code with code {string}")
    public void thereIsNoPromoCodeWithCode(String code) {
        when(promoCodeRepository.findByCode(code))
                .thenReturn(Optional.empty());

        assertFalse(promoCodeRepository.findByCode(code).isPresent());
    }

    @When("Method getPromoCodeByName called with code {string}")
    public void methodGetPromoCodeByNameCalledWithCode(String code) {
        try {
            actualPromoCode = promoCodeService.getPromoCodeByName(code);
        } catch (PromoCodeNotFoundException ex) {
            exception = ex;
        }
    }

    @Then("The response should contain the details of promo code with code {string}")
    public void theResponseShouldContainTheDetailsOfPromoCodeWithCode(String code) {
        expectedPromoCode = promoCodeRepository.findByCode(code).get();

        assertThat(actualPromoCode).isEqualTo(expectedPromoCode);
    }

    @Given("Editing promo code with id {long} exists and code {string} unique")
    public void editingPromoCodeWithIdExistsAndCodeUnique(long id, String code) {
        promoCodeRequest = TestPromoCodeUtil.getPromoCodeRequest();
        promoCode = TestPromoCodeUtil.getFirstPromoCode();
        PromoCode updatedPromoCode = TestPromoCodeUtil.getSecondPromoCode();
        updatedPromoCode.setCode(code);
        PromoCodeResponse expected = TestPromoCodeUtil.getSecondPromoCodeResponse();

        when(modelMapper.map(promoCodeRequest, PromoCode.class))
                .thenReturn(updatedPromoCode);
        when(promoCodeRepository.findById(id))
                .thenReturn(Optional.of(promoCode));
        when(promoCodeRepository.findByCode(code))
                .thenReturn(Optional.empty());
        when(promoCodeRepository.save(any(PromoCode.class)))
                .thenReturn(updatedPromoCode);
        when(modelMapper.map(updatedPromoCode, PromoCodeResponse.class))
                .thenReturn(expected);

        assertTrue(promoCodeRepository.findById(id)
                .isPresent());
        assertFalse(promoCodeRepository.findByCode(code)
                .isPresent());
    }

    @Given("Editing promo code with id {long} has invalid dates: start date {string}, end date {string}")
    public void editingPromoCodeHasInvalidDatesStartDateEndDate(long id, String startDate, String endDate) {
        promoCodeRequest = TestPromoCodeUtil.getPromoCodeRequest();
        promoCodeRequest.setStartDate(LocalDate.parse(startDate));
        promoCodeRequest.setEndDate(LocalDate.parse(endDate));
        promoCode = TestPromoCodeUtil.getFirstPromoCode();

        when(promoCodeRepository.findById(id))
                .thenReturn(Optional.of(promoCode));

        assertTrue(promoCodeRepository.findById(id)
                .isPresent());
    }

    @Given("Editing promo code with id {long} and code {string} already exists with later end date")
    public void editingPromoCodeAlreadyExistsWithLaterEndDate(long id, String code) {
        promoCodeRequest = TestPromoCodeUtil.getPromoCodeRequest();
        promoCodeRequest.setCode(code);
        promoCode = TestPromoCodeUtil.getFirstPromoCode();
        PromoCode newPromoCode = TestPromoCodeUtil.getSecondPromoCode();
        newPromoCode.setCode(code);

        when(promoCodeRepository.findById(id))
                .thenReturn(Optional.of(promoCode));
        when(promoCodeRepository.findByCode(code))
                .thenReturn(Optional.of(newPromoCode));

        assertTrue(promoCodeRepository.findById(id).isPresent());
        assertTrue(promoCodeRepository.findByCode(code).isPresent());
    }

    @When("Method editPromoCode called with id {long}")
    public void methodEditPromoCodeCalledWithId(long id) {
        try {
            actual = promoCodeService.editPromoCode(id, promoCodeRequest);
        } catch (IncorrectDateException | PromoCodeAlreadyExistsException | PromoCodeNotFoundException ex) {
            exception = ex;
        }
    }

    @Then("The response should contain the details of the edited promo code")
    public void theResponseShouldContainTheDetailsOfTheEditedPromoCode() {
        promoCode = promoCodeRepository.save(promoCode);
        expected = promoCodeService.mapPromoCodeToPromoCodeResponse(promoCode);

        assertEquals(expected, actual);
    }

    @Given("There are promo codes in the system")
    public void thereArePromoCodesInTheSystem() {
        List<PromoCode> promoCodes = TestPromoCodeUtil.getPromoCodes();
        List<PromoCodeResponse> promoCodeResponses = TestPromoCodeUtil.getPromoCodeResponses();

        when(promoCodeRepository.findAll())
                .thenReturn(promoCodes);
        when(promoCodeService.mapPromoCodeToPromoCodeResponse(promoCodes.get(0)))
                .thenReturn(promoCodeResponses.get(0));
        when(promoCodeService.mapPromoCodeToPromoCodeResponse(promoCodes.get(1)))
                .thenReturn(promoCodeResponses.get(1));

        assertFalse(promoCodeRepository.findAll().isEmpty());
    }

    @When("Method getAllPromoCodes called")
    public void methodGetAllPromoCodesCalled() {
        actualAllPromoCodesResponse = promoCodeService.getAllPromoCodes();
    }

    @Then("The response should contain a list of promo codes")
    public void theResponseShouldContainAListOfPromoCodes() {
        List<PromoCodeResponse> promoCodeResponses = TestPromoCodeUtil.getPromoCodeResponses();

        AllPromoCodesResponse expected = AllPromoCodesResponse.builder()
                .promoCodes(promoCodeResponses)
                .build();

        assertEquals(expected, actualAllPromoCodesResponse);
    }
}
