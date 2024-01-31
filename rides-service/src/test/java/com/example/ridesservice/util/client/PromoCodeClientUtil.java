package com.example.ridesservice.util.client;

import com.example.ridesservice.dto.request.PromoCodeRequest;
import com.example.ridesservice.dto.response.AllPromoCodesResponse;
import com.example.ridesservice.dto.response.ExceptionResponse;
import com.example.ridesservice.dto.response.PromoCodeResponse;
import com.example.ridesservice.dto.response.ValidationErrorResponse;
import io.restassured.http.ContentType;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;

@UtilityClass
public class PromoCodeClientUtil {
    private final String PROMO_CODE_SERVICE_URL = "ride/promo-code";
    private final String ID_PARAMETER_NAME = "id";

    public PromoCodeResponse createPromoCodeWhenPhoneNumberUniqueAndDataValidRequest(int port,
                                                                                     PromoCodeRequest promoCodeRequest) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(promoCodeRequest)
                .when()
                .post(PROMO_CODE_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(PromoCodeResponse.class);
    }

    public ExceptionResponse createPromoCodeWhenPromoCodeAlreadyExistsRequest(int port,
                                                                              PromoCodeRequest promoCodeRequest) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(promoCodeRequest)
                .when()
                .post(PROMO_CODE_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .extract()
                .as(ExceptionResponse.class);
    }

    public ValidationErrorResponse createPromoCodeWhenDataNotValidRequest(int port, PromoCodeRequest promoCodeRequest) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(promoCodeRequest)
                .when()
                .post(PROMO_CODE_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ValidationErrorResponse.class);
    }

    public PromoCodeResponse editPromoCodeWhenValidDataRequest(int port,
                                                               PromoCodeRequest promoCodeRequest,
                                                               Long promoCodeId) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(promoCodeRequest)
                .pathParam(ID_PARAMETER_NAME, promoCodeId)
                .when()
                .put(PROMO_CODE_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(PromoCodeResponse.class);
    }

    public ValidationErrorResponse editPromoCodeWhenInvalidDataRequest(int port,
                                                                       PromoCodeRequest promoCodeRequest,
                                                                       Long promoCodeId) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(promoCodeRequest)
                .pathParam(ID_PARAMETER_NAME, promoCodeId)
                .when()
                .put(PROMO_CODE_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ValidationErrorResponse.class);
    }

    public ExceptionResponse editPromoCodeWhenPromoCodeNotFoundRequest(int port,
                                                                       PromoCodeRequest promoCodeRequest,
                                                                       Long invalidPromoCodeId) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(promoCodeRequest)
                .pathParam(ID_PARAMETER_NAME, invalidPromoCodeId)
                .when()
                .put(PROMO_CODE_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);
    }

    public PromoCodeResponse getPromoCodeByIdWhenPromoCodeExistsRequest(int port, Long existingPromoCodeId) {
        return given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, existingPromoCodeId)
                .when()
                .get(PROMO_CODE_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(PromoCodeResponse.class);
    }

    public ExceptionResponse getPromoCodeByIdWhenPromoCodeNotExistsRequest(int port, Long invalidPromoCodeId) {
        return given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, invalidPromoCodeId)
                .when()
                .get(PROMO_CODE_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);
    }

    public AllPromoCodesResponse getAllPromoCodesRequest(int port) {
        return given()
                .port(port)
                .when()
                .get(PROMO_CODE_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(AllPromoCodesResponse.class);
    }
}
