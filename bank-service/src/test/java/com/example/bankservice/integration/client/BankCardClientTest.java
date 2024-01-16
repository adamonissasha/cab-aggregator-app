package com.example.bankservice.integration.client;

import com.example.bankservice.dto.request.BankCardRequest;
import com.example.bankservice.dto.request.RefillRequest;
import com.example.bankservice.dto.request.UpdateBankCardRequest;
import com.example.bankservice.dto.request.WithdrawalRequest;
import com.example.bankservice.dto.response.BalanceResponse;
import com.example.bankservice.dto.response.BankCardPageResponse;
import com.example.bankservice.dto.response.BankCardResponse;
import com.example.bankservice.dto.response.ExceptionResponse;
import com.example.bankservice.dto.response.ValidationErrorResponse;
import com.example.bankservice.model.enums.BankUser;
import io.restassured.http.ContentType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import static io.restassured.RestAssured.given;

@Component
public class BankCardClientTest {
    private static final String BANK_CARD_SERVICE_URL = "bank/card";
    private static final String ID_PARAMETER_NAME = "id";
    private static final String PAGE_PARAMETER_NAME = "page";
    private static final String SIZE_PARAMETER_NAME = "size";
    private static final String SORT_PARAMETER_NAME = "sortBy";
    private static final String BANK_USER_PARAMETER_NAME = "bankUser";

    public BankCardResponse createBankCardWhenNumberUniqueAndDataValidRequest(int port, BankCardRequest bankCardRequest) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(bankCardRequest)
                .when()
                .post(BANK_CARD_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(BankCardResponse.class);
    }

    public ExceptionResponse createBankCardWhenCardNumberAlreadyExistsRequest(int port, BankCardRequest bankCardRequest) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(bankCardRequest)
                .when()
                .post(BANK_CARD_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .extract()
                .as(ExceptionResponse.class);
    }

    public ValidationErrorResponse createBankCardWhenDataNotValidRequest(int port, BankCardRequest bankCardRequest) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(bankCardRequest)
                .when()
                .post(BANK_CARD_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ValidationErrorResponse.class);
    }

    public BankCardResponse editBankCardWhenValidDataRequest(int port, UpdateBankCardRequest bankCardRequest, Long bankCardId) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(bankCardRequest)
                .pathParam(ID_PARAMETER_NAME, bankCardId)
                .when()
                .put(BANK_CARD_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(BankCardResponse.class);
    }

    public ValidationErrorResponse editBankCardWhenInvalidDataRequest(int port, BankCardRequest bankCardRequest, Long bankCardId) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(bankCardRequest)
                .pathParam(ID_PARAMETER_NAME, bankCardId)
                .when()
                .put(BANK_CARD_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ValidationErrorResponse.class);
    }

    public ExceptionResponse editBankCardWhenBankCardNotFoundRequest(int port, BankCardRequest bankCardRequest, Long invalidBankCardId) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(bankCardRequest)
                .pathParam(ID_PARAMETER_NAME, invalidBankCardId)
                .when()
                .put(BANK_CARD_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);
    }

    public BankCardResponse getBankCardByIdWhenBankCardExistsRequest(int port, Long existingBankCardId) {
        return given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, existingBankCardId)
                .when()
                .get(BANK_CARD_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(BankCardResponse.class);
    }

    public ExceptionResponse getBankCardByIdWhenBankCardNotExistsRequest(int port, Long invalidBankCardId) {
        return given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, invalidBankCardId)
                .when()
                .get(BANK_CARD_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);
    }

    public BankCardPageResponse getAllBankCardsRequest(int port, int page, int size, String sortBy, Long bankUserId) {
        return given()
                .port(port)
                .param(PAGE_PARAMETER_NAME, page)
                .param(SIZE_PARAMETER_NAME, size)
                .param(SORT_PARAMETER_NAME, sortBy)
                .param(BANK_USER_PARAMETER_NAME, BankUser.PASSENGER)
                .pathParam(ID_PARAMETER_NAME, bankUserId)
                .when()
                .get(BANK_CARD_SERVICE_URL + "/user/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(BankCardPageResponse.class);
    }

    public ExceptionResponse getAllBankCardsWhenIncorrectFieldRequest(int port, int page, int size,
                                                                      String sortBy, Long bankUserId) {
        return given()
                .port(port)
                .param(PAGE_PARAMETER_NAME, page)
                .param(SIZE_PARAMETER_NAME, size)
                .param(SORT_PARAMETER_NAME, sortBy)
                .param(BANK_USER_PARAMETER_NAME, BankUser.PASSENGER)
                .pathParam(ID_PARAMETER_NAME, bankUserId)
                .when()
                .get(BANK_CARD_SERVICE_URL + "/user/{id}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ExceptionResponse.class);
    }

    public void deleteBankCardWhenBankCardExistsRequest(int port, Long existingBankCardId) {
        given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, existingBankCardId)
                .when()
                .delete(BANK_CARD_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    public void deleteBankCardWhenBankCardNotExistsRequest(int port, Long invalidBankCardId) {
        given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, invalidBankCardId)
                .when()
                .delete(BANK_CARD_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    public void deleteBankUserCardsRequest(int port, Long existingBankCardId) {
        given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, existingBankCardId)
                .param(BANK_USER_PARAMETER_NAME, BankUser.PASSENGER)
                .when()
                .delete(BANK_CARD_SERVICE_URL + "/user/{id}")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    public BankCardResponse makeBankCardDefaultWhenBankCardExistsRequest(int port, Long bankCardId) {
        return given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, bankCardId)
                .when()
                .put(BANK_CARD_SERVICE_URL + "/default/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(BankCardResponse.class);
    }

    public ExceptionResponse makeBankCardDefaultWhenBankCardNotExistsRequest(int port, Long invalidBankCardId) {
        return given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, invalidBankCardId)
                .when()
                .put(BANK_CARD_SERVICE_URL + "/default/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);
    }

    public BankCardResponse getDefaultBankCardWhenDefaultBankCardExistsRequest(int port, Long bankUserId) {
        return given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, bankUserId)
                .param(BANK_USER_PARAMETER_NAME, BankUser.PASSENGER)
                .when()
                .get(BANK_CARD_SERVICE_URL + "/user/{id}/default")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(BankCardResponse.class);
    }

    public ExceptionResponse getDefaultBankCardWhenDefaultBankCardNotExistsRequest(int port, Long bankUserId) {
        return given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, bankUserId)
                .param(BANK_USER_PARAMETER_NAME, BankUser.DRIVER)
                .when()
                .get(BANK_CARD_SERVICE_URL + "/user/{id}/default")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);
    }

    public BalanceResponse getBankCardBalanceWhenBankCardExistsRequest(int port, Long existingBankCardId) {
        return given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, existingBankCardId)
                .when()
                .get(BANK_CARD_SERVICE_URL + "/{id}/balance")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(BalanceResponse.class);
    }

    public ExceptionResponse getBankCardBalanceWhenBankCardNotExistsRequest(int port, Long invalidBankCardId) {
        return given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, invalidBankCardId)
                .when()
                .get(BANK_CARD_SERVICE_URL + "/{id}/balance")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);
    }

    public BankCardResponse refillBankCardWhenBankCardExistsRequest(int port, Long bankCardId, RefillRequest refillRequest) {
        return given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, bankCardId)
                .contentType(ContentType.JSON)
                .body(refillRequest)
                .when()
                .put(BANK_CARD_SERVICE_URL + "/{id}/refill")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(BankCardResponse.class);
    }

    public ExceptionResponse refillBankCardWhenBankCardNotExistsRequest(int port, Long invalidBankCardId, RefillRequest refillRequest) {
        return given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, invalidBankCardId)
                .contentType(ContentType.JSON)
                .body(refillRequest)
                .when()
                .put(BANK_CARD_SERVICE_URL + "/{id}/refill")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);
    }

    public BankCardResponse withdrawalPaymentFromBankCardWhenBankCardExistsRequest(int port, Long bankCardId, WithdrawalRequest withdrawalRequest) {
        return given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, bankCardId)
                .contentType(ContentType.JSON)
                .body(withdrawalRequest)
                .when()
                .put(BANK_CARD_SERVICE_URL + "/{id}/withdrawal")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(BankCardResponse.class);
    }

    public ExceptionResponse withdrawalPaymentFromBankCardWhenSumMoreThanBalanceRequest(int port, Long bankCardId, WithdrawalRequest withdrawalRequest) {
        return given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, bankCardId)
                .contentType(ContentType.JSON)
                .body(withdrawalRequest)
                .when()
                .put(BANK_CARD_SERVICE_URL + "/{id}/withdrawal")
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .extract()
                .as(ExceptionResponse.class);
    }

    public ExceptionResponse withdrawalPaymentFromBankCardWhenBankCardNotExistsRequest(int port, Long invalidBankCardId, WithdrawalRequest withdrawalRequest) {
        return given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, invalidBankCardId)
                .contentType(ContentType.JSON)
                .body(withdrawalRequest)
                .when()
                .put(BANK_CARD_SERVICE_URL + "/{id}/withdrawal")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);
    }
}
