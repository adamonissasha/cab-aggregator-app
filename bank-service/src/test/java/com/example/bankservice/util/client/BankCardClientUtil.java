package com.example.bankservice.util.client;

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
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BankCardClientUtil {
    static String BANK_CARD_SERVICE_URL = "bank/card";
    static String ID_PARAMETER_NAME = "id";
    static String PAGE_PARAMETER_NAME = "page";
    static String SIZE_PARAMETER_NAME = "size";
    static String SORT_PARAMETER_NAME = "sortBy";
    static String BANK_USER_PARAMETER_NAME = "bankUser";

    public static BankCardResponse createBankCardWhenNumberUniqueAndDataValidRequest(int port,
                                                                                     BankCardRequest bankCardRequest) {
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

    public static ExceptionResponse createBankCardWhenCardNumberAlreadyExistsRequest(int port,
                                                                                     BankCardRequest bankCardRequest) {
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

    public static ValidationErrorResponse createBankCardWhenDataNotValidRequest(int port,
                                                                                BankCardRequest bankCardRequest) {
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

    public static BankCardResponse editBankCardWhenValidDataRequest(int port,
                                                                    UpdateBankCardRequest bankCardRequest,
                                                                    Long bankCardId) {
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

    public static ValidationErrorResponse editBankCardWhenInvalidDataRequest(int port,
                                                                             BankCardRequest bankCardRequest,
                                                                             Long bankCardId) {
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

    public static ExceptionResponse editBankCardWhenBankCardNotFoundRequest(int port,
                                                                            BankCardRequest bankCardRequest,
                                                                            Long invalidBankCardId) {
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

    public static BankCardResponse getBankCardByIdWhenBankCardExistsRequest(int port, Long existingBankCardId) {
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

    public static ExceptionResponse getBankCardByIdWhenBankCardNotExistsRequest(int port, Long invalidBankCardId) {
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

    public static BankCardPageResponse getAllBankCardsRequest(int port,
                                                              int page,
                                                              int size,
                                                              String sortBy,
                                                              Long bankUserId) {
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

    public static ExceptionResponse getAllBankCardsWhenIncorrectFieldRequest(int port,
                                                                             int page,
                                                                             int size,
                                                                             String sortBy,
                                                                             Long bankUserId) {
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

    public static void deleteBankCardWhenBankCardExistsRequest(int port, Long existingBankCardId) {
        given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, existingBankCardId)
                .when()
                .delete(BANK_CARD_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    public static void deleteBankCardWhenBankCardNotExistsRequest(int port, Long invalidBankCardId) {
        given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, invalidBankCardId)
                .when()
                .delete(BANK_CARD_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    public static void deleteBankUserCardsRequest(int port, Long existingBankCardId) {
        given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, existingBankCardId)
                .param(BANK_USER_PARAMETER_NAME, BankUser.PASSENGER)
                .when()
                .delete(BANK_CARD_SERVICE_URL + "/user/{id}")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    public static BankCardResponse makeBankCardDefaultWhenBankCardExistsRequest(int port, Long bankCardId) {
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

    public static ExceptionResponse makeBankCardDefaultWhenBankCardNotExistsRequest(int port, Long invalidBankCardId) {
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

    public static BankCardResponse getDefaultBankCardWhenDefaultBankCardExistsRequest(int port, Long bankUserId) {
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

    public static ExceptionResponse getDefaultBankCardWhenDefaultBankCardNotExistsRequest(int port, Long bankUserId) {
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

    public static BalanceResponse getBankCardBalanceWhenBankCardExistsRequest(int port, Long existingBankCardId) {
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

    public static ExceptionResponse getBankCardBalanceWhenBankCardNotExistsRequest(int port, Long invalidBankCardId) {
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

    public static BankCardResponse refillBankCardWhenBankCardExistsRequest(int port,
                                                                           Long bankCardId,
                                                                           RefillRequest refillRequest) {
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

    public static ExceptionResponse refillBankCardWhenBankCardNotExistsRequest(int port,
                                                                               Long invalidBankCardId,
                                                                               RefillRequest refillRequest) {
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

    public static BankCardResponse withdrawalPaymentFromBankCardWhenBankCardExistsRequest(int port,
                                                                                          Long bankCardId,
                                                                                          WithdrawalRequest withdrawalRequest) {
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

    public static ExceptionResponse withdrawalPaymentFromBankCardWhenSumMoreThanBalanceRequest(int port,
                                                                                               Long bankCardId,
                                                                                               WithdrawalRequest withdrawalRequest) {
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

    public static ExceptionResponse withdrawalPaymentFromBankCardWhenBankCardNotExistsRequest(int port,
                                                                                              Long invalidBankCardId,
                                                                                              WithdrawalRequest withdrawalRequest) {
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
