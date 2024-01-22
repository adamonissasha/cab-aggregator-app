package com.example.bankservice.util.client;

import com.example.bankservice.dto.request.BankAccountHistoryRequest;
import com.example.bankservice.dto.response.BankAccountHistoryPageResponse;
import com.example.bankservice.dto.response.BankAccountHistoryResponse;
import com.example.bankservice.dto.response.ExceptionResponse;
import io.restassured.http.ContentType;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BankAccountHistoryClientUtil {
    static String BANK_ACCOUNT_SERVICE_URL = "bank/account/{id}/history";
    static String ID_PARAMETER_NAME = "id";
    static String PAGE_PARAMETER_NAME = "page";
    static String SIZE_PARAMETER_NAME = "size";
    static String SORT_PARAMETER_NAME = "sortBy";

    public static BankAccountHistoryResponse createBankAccountHistoryRequest(int port,
                                                                             BankAccountHistoryRequest bankAccountHistoryRequest,
                                                                             Long bankAccountId) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(bankAccountHistoryRequest)
                .pathParam(ID_PARAMETER_NAME, bankAccountId)
                .when()
                .post(BANK_ACCOUNT_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(BankAccountHistoryResponse.class);
    }

    public static BankAccountHistoryPageResponse getAllBankAccountHistoryRecordsRequest(int port, int page, int size,
                                                                                        String sortBy, Long bankAccountId) {
        return given()
                .port(port)
                .param(PAGE_PARAMETER_NAME, page)
                .param(SIZE_PARAMETER_NAME, size)
                .param(SORT_PARAMETER_NAME, sortBy)
                .pathParam(ID_PARAMETER_NAME, bankAccountId)
                .when()
                .get(BANK_ACCOUNT_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(BankAccountHistoryPageResponse.class);
    }

    public static ExceptionResponse getAllBankAccountHistoryRecordsWhenIncorrectFieldRequest(int port, int page, int size,
                                                                                             String sortBy, Long bankAccountId) {
        return given()
                .port(port)
                .param(PAGE_PARAMETER_NAME, page)
                .param(SIZE_PARAMETER_NAME, size)
                .param(SORT_PARAMETER_NAME, sortBy)
                .pathParam(ID_PARAMETER_NAME, bankAccountId)
                .when()
                .get(BANK_ACCOUNT_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ExceptionResponse.class);
    }
}
