package com.example.bankservice.util.client;

import com.example.bankservice.dto.request.BankAccountHistoryRequest;
import com.example.bankservice.dto.response.BankAccountHistoryPageResponse;
import com.example.bankservice.dto.response.BankAccountHistoryResponse;
import com.example.bankservice.dto.response.ExceptionResponse;
import io.restassured.http.ContentType;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;

@UtilityClass
public class BankAccountHistoryClientUtil {
    private final String BANK_ACCOUNT_SERVICE_URL = "bank/account/{id}/history";
    private final String ID_PARAMETER_NAME = "id";
    private final String PAGE_PARAMETER_NAME = "page";
    private final String SIZE_PARAMETER_NAME = "size";
    private final String SORT_PARAMETER_NAME = "sortBy";

    public BankAccountHistoryResponse createBankAccountHistoryRequest(int port,
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

    public BankAccountHistoryPageResponse getAllBankAccountHistoryRecordsRequest(int port, int page, int size,
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

    public ExceptionResponse getAllBankAccountHistoryRecordsWhenIncorrectFieldRequest(int port, int page, int size,
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
