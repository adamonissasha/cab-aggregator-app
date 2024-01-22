package com.example.ridesservice.integration;

import com.example.ridesservice.dto.request.PromoCodeRequest;
import com.example.ridesservice.dto.response.AllPromoCodesResponse;
import com.example.ridesservice.dto.response.ExceptionResponse;
import com.example.ridesservice.dto.response.PromoCodeResponse;
import com.example.ridesservice.dto.response.ValidationErrorResponse;
import com.example.ridesservice.repository.PromoCodeRepository;
import com.example.ridesservice.util.TestPromoCodeUtil;
import com.example.ridesservice.util.client.PromoCodeClientUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "classpath:sql/add-test-data.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:sql/delete-test-data.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Testcontainers
public class PromoCodeControllerTest {
    @LocalServerPort
    private int port;

    private final PromoCodeRepository promoCodeRepository;

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Autowired
    public PromoCodeControllerTest(PromoCodeRepository promoCodeRepository) {
        this.promoCodeRepository = promoCodeRepository;
    }

    @Test
    void createPromoCode_WhenPhoneNumberUniqueAndDataValid_ShouldReturnPromoCodeResponse() {
        PromoCodeRequest promoCodeRequest = TestPromoCodeUtil.getNewPromoCodeRequest();
        PromoCodeResponse expected = TestPromoCodeUtil.getNewPromoCodeResponse();

        PromoCodeResponse actual =
                PromoCodeClientUtil.createPromoCodeWhenPhoneNumberUniqueAndDataValidRequest(port, promoCodeRequest);

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);

        deletePromoCodeAfterTest(actual.getId());
    }

    @Test
    void createPromoCode_WhenPromoCodeAlreadyExists_ShouldReturnConflictResponse() {
        PromoCodeRequest promoCodeRequest = TestPromoCodeUtil.getPromoCodeRequest();
        ExceptionResponse expected = TestPromoCodeUtil.getPromoCodeExistsExceptionResponse();

        ExceptionResponse actual =
                PromoCodeClientUtil.createPromoCodeWhenPromoCodeAlreadyExistsRequest(port, promoCodeRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void createPromoCode_WhenDataNotValid_ShouldReturnBadRequestResponse() {
        PromoCodeRequest promoCodeRequest = TestPromoCodeUtil.getPromoCodeRequestWithInvalidData();
        ValidationErrorResponse expected = TestPromoCodeUtil.getValidationErrorResponse();

        ValidationErrorResponse actual =
                PromoCodeClientUtil.createPromoCodeWhenDataNotValidRequest(port, promoCodeRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void editPromoCode_WhenValidData_ShouldReturnPromoCodeResponse() {
        Long promoCodeId = TestPromoCodeUtil.getFirstPromoCodeId();
        PromoCodeRequest promoCodeRequest = TestPromoCodeUtil.getNewPromoCodeRequest();
        PromoCodeResponse expected = TestPromoCodeUtil.getNewPromoCodeResponse();
        expected.setId(promoCodeId);

        PromoCodeResponse actual =
                PromoCodeClientUtil.editPromoCodeWhenValidDataRequest(port, promoCodeRequest, promoCodeId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void editPromoCode_WhenInvalidData_ShouldReturnBadRequestResponse() {
        Long promoCodeId = TestPromoCodeUtil.getFirstPromoCodeId();
        PromoCodeRequest promoCodeRequest = TestPromoCodeUtil.getPromoCodeRequestWithInvalidData();
        ValidationErrorResponse expected = TestPromoCodeUtil.getValidationErrorResponse();

        ValidationErrorResponse actual =
                PromoCodeClientUtil.editPromoCodeWhenInvalidDataRequest(port, promoCodeRequest, promoCodeId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void editPromoCode_WhenPromoCodeNotFound_ShouldReturnNotFoundResponse() {
        Long invalidPromoCodeId = TestPromoCodeUtil.getInvalidId();
        PromoCodeRequest promoCodeRequest = TestPromoCodeUtil.getNewPromoCodeRequest();
        ExceptionResponse expected = TestPromoCodeUtil.getPromoCodeNotFoundExceptionResponse();

        ExceptionResponse actual =
                PromoCodeClientUtil.editPromoCodeWhenPromoCodeNotFoundRequest(port, promoCodeRequest, invalidPromoCodeId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getPromoCodeById_WhenPromoCodeExists_ShouldReturnPromoCodeResponse() {
        Long existingPromoCodeId = TestPromoCodeUtil.getFirstPromoCodeId();
        PromoCodeResponse expected = TestPromoCodeUtil.getFirstPromoCodeResponse();

        PromoCodeResponse actual =
                PromoCodeClientUtil.getPromoCodeByIdWhenPromoCodeExistsRequest(port, existingPromoCodeId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getPromoCodeById_WhenPromoCodeNotExists_ShouldReturnNotFoundResponse() {
        Long invalidPromoCodeId = TestPromoCodeUtil.getInvalidId();
        ExceptionResponse expected = TestPromoCodeUtil.getPromoCodeNotFoundExceptionResponse();

        ExceptionResponse actual =
                PromoCodeClientUtil.getPromoCodeByIdWhenPromoCodeNotExistsRequest(port, invalidPromoCodeId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getAllPromoCodes_ShouldReturnAllPromoCodesResponse() {
        AllPromoCodesResponse expected = TestPromoCodeUtil.getAllPromoCodesResponse();

        AllPromoCodesResponse actual = PromoCodeClientUtil.getAllPromoCodesRequest(port);

        assertThat(actual).isEqualTo(expected);
    }

    void deletePromoCodeAfterTest(Long id) {
        promoCodeRepository.deleteById(id);
    }
}
