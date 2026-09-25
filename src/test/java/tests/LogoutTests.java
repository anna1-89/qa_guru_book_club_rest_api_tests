package tests;

import models.login.LoginBodyModel;
import models.logout.EmptyTokenLogoutResponseModel;
import models.logout.InvalidTokenLogoutResponseModel;
import models.logout.LogoutBodyModel;
import models.logout.SuccessfulLogoutResponseModel;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.BaseSpec.baseRequestSpec;
import static specs.login.LoginSpec.successfulLoginResponseSpec;
import static specs.logout.LogoutSpec.*;
import static tests.TestData.*;

public class LogoutTests extends TestBase {

    @Test
    public void successfulLogoutTest() {
        LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD);

        String refreshToken = step("Авторизация и получение токена", () ->
            given(baseRequestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(successfulLoginResponseSpec)
                    .extract().path("refresh"));

        step("Отправка запроса logout с refresh-токеном и проверка ответа (200)", () -> {
            LogoutBodyModel logoutData = new LogoutBodyModel(refreshToken);

            SuccessfulLogoutResponseModel logoutResponse = given(baseRequestSpec)
                    .body(logoutData)
                    .when()
                    .post("/auth/logout/")
                    .then()
                    .spec(successfulLogoutResponseSpec)
                    .extract().as(SuccessfulLogoutResponseModel.class);
        });
    }

    @Test
    public void emptyTokenLogoutTest() {
        LogoutBodyModel logoutData = new LogoutBodyModel("");
        EmptyTokenLogoutResponseModel logoutResponse = given(baseRequestSpec)
                .body(logoutData)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(emptyTokenLogoutResponseSpec)
                .extract().as(EmptyTokenLogoutResponseModel.class);

        String expectedError = LOGOUT_EMPTY_TOKEN_ERROR;
        String actualError = logoutResponse.refresh().get(0);
        assertThat(actualError).isEqualTo(expectedError);
    }

    @Test
    public void invalidTokenLogoutTest() {
        LogoutBodyModel logoutData = new LogoutBodyModel(LOGOUT_WRONG_TOKEN);
        InvalidTokenLogoutResponseModel logoutResponse = given(baseRequestSpec)
                .body(logoutData)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(invalidTokenLogoutResponseSpec)
                .extract().as(InvalidTokenLogoutResponseModel.class);

        String expectedError = LOGOUT_WRONG_TOKEN_ERROR;
        String expectedCodeError = LOGOUT_WRONG_TOKEN_CODE_ERROR;
        String actualError = logoutResponse.detail();
        String actualCodeError = logoutResponse.code();
        assertThat(actualError).isEqualTo(expectedError);
        assertThat(actualCodeError).isEqualTo(expectedCodeError);
    }

    @Test
    public void expiredTokenLogoutTest() {
        LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD);
        String refreshToken = given(baseRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract().path("refresh");

        LogoutBodyModel logoutData = new LogoutBodyModel(refreshToken);
        SuccessfulLogoutResponseModel firstLogoutResponse = given(baseRequestSpec)
                .body(logoutData)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(successfulLogoutResponseSpec)
                .extract().as(SuccessfulLogoutResponseModel.class);

        InvalidTokenLogoutResponseModel secondLogoutResponse = given(baseRequestSpec)
                .body(logoutData)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(invalidTokenLogoutResponseSpec)
                .extract().as(InvalidTokenLogoutResponseModel.class);

        String expectedError = LOGOUT_BLACKLISTED_TOKEN_ERROR;
        String expectedCodeError = LOGOUT_WRONG_TOKEN_CODE_ERROR;
        String actualError = secondLogoutResponse.detail();
        String actualCodeError = secondLogoutResponse.code();
        assertThat(actualError).isEqualTo(expectedError);
        assertThat(actualCodeError).isEqualTo(expectedCodeError);
    }




}
