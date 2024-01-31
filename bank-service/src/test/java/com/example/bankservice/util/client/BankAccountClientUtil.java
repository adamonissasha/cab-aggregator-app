package com.example.bankservice.util.client;

import com.example.bankservice.dto.request.BankAccountRequest;
import com.example.bankservice.dto.request.RefillRequest;
import com.example.bankservice.dto.request.WithdrawalRequest;
import com.example.bankservice.dto.response.BalanceResponse;
import com.example.bankservice.dto.response.BankAccountPageResponse;
import com.example.bankservice.dto.response.BankAccountResponse;
import com.example.bankservice.dto.response.ExceptionResponse;
import com.example.bankservice.dto.response.ValidationErrorResponse;
import io.restassured.http.ContentType;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;

@UtilityClass
public class BankAccountClientUtil {
    private final String BANK_ACCOUNT_SERVICE_URL = "bank/account";
    private final String ID_PARAMETER_NAME = "id";
    private final String PAGE_PARAMETER_NAME = "page";
    private final String SIZE_PARAMETER_NAME = "size";
    private final String SORT_PARAMETER_NAME = "sortBy";
    private final String BANK_USER_PARAMETER_NAME = "bankUser";

    public BankAccountResponse createBankAccountWhenNumberUniqueAndDataValidRequest(int port,
                                                                                    BankAccountRequest bankAccountRequest) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(bankAccountRequest)
                .when()
                .post(BANK_ACCOUNT_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(BankAccountResponse.class);
    }

    public ExceptionResponse createBankAccountWhenAccountNumberAlreadyExistsRequest(int port,
                                                                                    BankAccountRequest bankAccountRequest) {

        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(bankAccountRequest)
                .when()
                .post(BANK_ACCOUNT_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .extract()
                .as(ExceptionResponse.class);
    }

    public ExceptionResponse createBankAccountWhenDriverAlreadyHasAccountRequest(int port,
                                                                                 BankAccountRequest bankAccountRequest) {

        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(bankAccountRequest)
                .when()
                .post(BANK_ACCOUNT_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .extract()
                .as(ExceptionResponse.class);
    }

    public ValidationErrorResponse createBankAccountWhenDataNotValidRequest(int port,
                                                                            BankAccountRequest bankAccountRequest) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(bankAccountRequest)
                .when()
                .post(BANK_ACCOUNT_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ValidationErrorResponse.class);
    }


    public BankAccountResponse getBankAccountByIdWhenBankAccountExistsRequest(int port,
                                                                              Long existingBankAccountId) {
        return given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, existingBankAccountId)
                .when()
                .get(BANK_ACCOUNT_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(BankAccountResponse.class);
    }

    public ExceptionResponse getBankAccountByIdWhenBankAccountNotExistsRequest(int port,
                                                                               Long invalidBankAccountId) {
        return given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, invalidBankAccountId)
                .when()
                .get(BANK_ACCOUNT_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);
    }

    public BankAccountPageResponse getAllBankAccountsRequest(int port, int page, int size, String sortBy) {
        return given()
                .port(port)
                .param(PAGE_PARAMETER_NAME, page)
                .param(SIZE_PARAMETER_NAME, size)
                .param(SORT_PARAMETER_NAME, sortBy)
                .param(BANK_USER_PARAMETER_NAME, "PASSENGER")
                .when()
                .get(BANK_ACCOUNT_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(BankAccountPageResponse.class);
    }

    public ExceptionResponse getAllBankAccountsWhenIncorrectFieldRequest(int port,
                                                                         int page,
                                                                         int size,
                                                                         String sortBy) {
        return given()
                .port(port)
                .param(PAGE_PARAMETER_NAME, page)
                .param(SIZE_PARAMETER_NAME, size)
                .param(SORT_PARAMETER_NAME, sortBy)
                .when()
                .get(BANK_ACCOUNT_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ExceptionResponse.class);
    }

    public BankAccountPageResponse getAllActiveBankAccountsRequest(int port, int page, int size, String sortBy) {
        return given()
                .port(port)
                .param(PAGE_PARAMETER_NAME, page)
                .param(SIZE_PARAMETER_NAME, size)
                .param(SORT_PARAMETER_NAME, sortBy)
                .param(BANK_USER_PARAMETER_NAME, "PASSENGER")
                .when()
                .get(BANK_ACCOUNT_SERVICE_URL + "/active")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(BankAccountPageResponse.class);
    }

    public ExceptionResponse getAllActiveBankAccountWhenIncorrectFieldRequest(int port,
                                                                              int page,
                                                                              int size,
                                                                              String sortBy) {
        return given()
                .port(port)
                .param(PAGE_PARAMETER_NAME, page)
                .param(SIZE_PARAMETER_NAME, size)
                .param(SORT_PARAMETER_NAME, sortBy)
                .when()
                .get(BANK_ACCOUNT_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ExceptionResponse.class);
    }

    public void deleteBankAccountWhenBankAccountExistsRequest(int port, Long driverId) {
        given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, driverId)
                .when()
                .delete(BANK_ACCOUNT_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    public void deleteBankAccountWhenBankAccountNotExistsRequest(int port, Long invalidBankAccountId) {
        given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, invalidBankAccountId)
                .when()
                .delete(BANK_ACCOUNT_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    public BalanceResponse getBankAccountBalanceWhenBankAccountExistsRequest(int port,
                                                                             Long existingBankAccountId) {
        return given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, existingBankAccountId)
                .when()
                .get(BANK_ACCOUNT_SERVICE_URL + "/{id}/balance")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(BalanceResponse.class);
    }

    public ExceptionResponse getBankAccountBalanceWhenBankAccountNotExistsRequest(int port,
                                                                                  Long invalidBankAccountId) {
        return given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, invalidBankAccountId)
                .when()
                .get(BANK_ACCOUNT_SERVICE_URL + "/{id}/balance")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);
    }

    public BankAccountResponse refillBankAccountWhenBankAccountExistsRequest(int port,
                                                                             RefillRequest refillRequest) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(refillRequest)
                .when()
                .put(BANK_ACCOUNT_SERVICE_URL + "/refill")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(BankAccountResponse.class);
    }

    public ExceptionResponse refillBankAccountWhenBankAccountNotExistsRequest(int port,
                                                                              RefillRequest refillRequest) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(refillRequest)
                .when()
                .put(BANK_ACCOUNT_SERVICE_URL + "/refill")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);
    }

    public BankAccountResponse withdrawalPaymentFromBankAccountWhenBankAccountExistsRequest(int port,
                                                                                            Long bankAccountId,
                                                                                            WithdrawalRequest withdrawalRequest) {
        return given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, bankAccountId)
                .contentType(ContentType.JSON)
                .body(withdrawalRequest)
                .when()
                .put(BANK_ACCOUNT_SERVICE_URL + "/{id}/withdrawal")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(BankAccountResponse.class);
    }

    public ExceptionResponse withdrawalPaymentFromBankAccountWhenSumMoreThanBalanceRequest(int port,
                                                                                           Long bankAccountId,
                                                                                           WithdrawalRequest withdrawalRequest) {
        return given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, bankAccountId)
                .contentType(ContentType.JSON)
                .body(withdrawalRequest)
                .when()
                .put(BANK_ACCOUNT_SERVICE_URL + "/{id}/withdrawal")
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .extract()
                .as(ExceptionResponse.class);
    }

    public ExceptionResponse withdrawalPaymentFromBankAccountWhenSumIsOutsideBorderRequest(int port,
                                                                                           Long bankAccountId,
                                                                                           WithdrawalRequest withdrawalRequest) {
        return given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, bankAccountId)
                .contentType(ContentType.JSON)
                .body(withdrawalRequest)
                .when()
                .put(BANK_ACCOUNT_SERVICE_URL + "/{id}/withdrawal")
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .extract()
                .as(ExceptionResponse.class);
    }

    public ExceptionResponse withdrawalPaymentFromBankAccountWhenBankAccountNotExistsRequest(int port,
                                                                                             Long invalidBankAccountId,
                                                                                             WithdrawalRequest withdrawalRequest) {
        return given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, invalidBankAccountId)
                .contentType(ContentType.JSON)
                .body(withdrawalRequest)
                .when()
                .put(BANK_ACCOUNT_SERVICE_URL + "/{id}/withdrawal")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);
    }
}
