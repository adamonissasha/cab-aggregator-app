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
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BankAccountClientUtil {
    static String BANK_ACCOUNT_SERVICE_URL = "bank/account";
    static String ID_PARAMETER_NAME = "id";
    static String PAGE_PARAMETER_NAME = "page";
    static String SIZE_PARAMETER_NAME = "size";
    static String SORT_PARAMETER_NAME = "sortBy";
    static String BANK_USER_PARAMETER_NAME = "bankUser";

    public static BankAccountResponse createBankAccountWhenNumberUniqueAndDataValidRequest(int port,
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

    public static ExceptionResponse createBankAccountWhenAccountNumberAlreadyExistsRequest(int port,
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

    public static ExceptionResponse createBankAccountWhenDriverAlreadyHasAccountRequest(int port,
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

    public static ValidationErrorResponse createBankAccountWhenDataNotValidRequest(int port,
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


    public static BankAccountResponse getBankAccountByIdWhenBankAccountExistsRequest(int port,
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

    public static ExceptionResponse getBankAccountByIdWhenBankAccountNotExistsRequest(int port,
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

    public static BankAccountPageResponse getAllBankAccountsRequest(int port, int page, int size, String sortBy) {
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

    public static ExceptionResponse getAllBankAccountsWhenIncorrectFieldRequest(int port,
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

    public static BankAccountPageResponse getAllActiveBankAccountsRequest(int port, int page, int size, String sortBy) {
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

    public static ExceptionResponse getAllActiveBankAccountWhenIncorrectFieldRequest(int port,
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

    public static void deleteBankAccountWhenBankAccountExistsRequest(int port, Long driverId) {
        given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, driverId)
                .when()
                .delete(BANK_ACCOUNT_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    public static void deleteBankAccountWhenBankAccountNotExistsRequest(int port, Long invalidBankAccountId) {
        given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, invalidBankAccountId)
                .when()
                .delete(BANK_ACCOUNT_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    public static BalanceResponse getBankAccountBalanceWhenBankAccountExistsRequest(int port,
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

    public static ExceptionResponse getBankAccountBalanceWhenBankAccountNotExistsRequest(int port,
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

    public static BankAccountResponse refillBankAccountWhenBankAccountExistsRequest(int port,
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

    public static ExceptionResponse refillBankAccountWhenBankAccountNotExistsRequest(int port,
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

    public static BankAccountResponse withdrawalPaymentFromBankAccountWhenBankAccountExistsRequest(int port,
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

    public static ExceptionResponse withdrawalPaymentFromBankAccountWhenSumMoreThanBalanceRequest(int port,
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

    public static ExceptionResponse withdrawalPaymentFromBankAccountWhenSumIsOutsideBorderRequest(int port,
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

    public static ExceptionResponse withdrawalPaymentFromBankAccountWhenBankAccountNotExistsRequest(int port,
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
