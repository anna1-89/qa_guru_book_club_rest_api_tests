package tests;

import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.login.WrongCredentialsLoginResponseModel;
import models.login.EmptyPasswordResponseModel;
import models.login.EmptyUserAndPasswordResponseModel;
import models.login.EmptyUsernameResponseModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.BaseSpec.baseRequestSpec;
import static specs.login.LoginSpec.*;
import static tests.TestData.*;

public class LoginTests extends TestBase {

    @Test
    @DisplayName("Успешный вход в систему")
    public void successfulLoginTest() {
        LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD);

        step("Отправка запроса login и проверка ответа (200)", () -> {
            SuccessfulLoginResponseModel loginResponse = given(baseRequestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(successfulLoginResponseSpec)
                    .extract().as(SuccessfulLoginResponseModel.class);

            String expectedTokenPath = LOGIN_TOKEN_PREFIX;
            String actualAccess = loginResponse.access();
            String actualRefresh = loginResponse.refresh();
            assertThat(actualAccess).startsWith(expectedTokenPath);
            assertThat(actualRefresh).startsWith(expectedTokenPath);
            assertThat(actualAccess).isNotEqualTo(actualRefresh);
        });
    }

    @Test
    @DisplayName("Ошибка при попытке входа в систему с невалидным паролем")
    public void wrongCredentialsPasswordLoginTest() {
        LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, LOGIN_WRONG_PASSWORD);

        step("Отправка запроса login с невалидеым паролем и проверка ответа (401, текст с ошибкой)", () -> {
            WrongCredentialsLoginResponseModel loginResponse = given(baseRequestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(wrongCredentialsLoginResponseSpec)
                    .extract().as(WrongCredentialsLoginResponseModel.class);

            String expectedDetailError = LOGIN_WRONG_CREDENTIALS_ERROR;
            String actualDetailError = loginResponse.detail();
            assertThat(actualDetailError).isEqualTo(expectedDetailError);
        });
    }

    @Test
    @DisplayName("Ошибка при попытке входа в систему с невалидным username")
    public void wrongCredentialUsernamesLoginTest() {
        LoginBodyModel loginData = new LoginBodyModel(LOGIN_WRONG_USERNAME, LOGIN_PASSWORD);

        step("Отправка запроса login с невалидеым username и проверка ответа (401, текст с ошибкой)", () -> {
            WrongCredentialsLoginResponseModel loginResponse = given(baseRequestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(wrongCredentialsLoginResponseSpec)
                    .extract().as(WrongCredentialsLoginResponseModel.class);

            String expectedDetailError = LOGIN_WRONG_CREDENTIALS_ERROR;
            String actualDetailError = loginResponse.detail();
            assertThat(actualDetailError).isEqualTo(expectedDetailError);
        });
    }

    @Test
    @DisplayName("Ошибка при попытке входа в систему с незаполненным username")
    public void emptyUserRegistrationTest() {
        LoginBodyModel loginData = new LoginBodyModel("", LOGIN_PASSWORD);

        step("Отправка запроса login с незаполненным username и проверка ответа (400, текст с ошибкой)", () -> {
            EmptyUsernameResponseModel loginResponse = given(baseRequestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(emptyUserLoginResponseSpec)
                    .extract().as(EmptyUsernameResponseModel.class);

            String expectedError = LOGIN_EMPTY_FIELD_ERROR;
            String actualError = loginResponse.username().get(0);
            assertThat(actualError).isEqualTo(expectedError);
        });
    }

    @Test
    @DisplayName("Ошибка при попытке входа в систему с незаполненным паролем")
    public void emptyPasswordRegistrationTest() {
        LoginBodyModel loginData = new LoginBodyModel(LOGIN_WRONG_USERNAME, "");

        step("Отправка запроса login с незаполненным паролем и проверка ответа (400, текст с ошибкой)", () -> {
        EmptyPasswordResponseModel loginResponse = given(baseRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(emptyPasswordLoginResponseSpec)
                .extract().as(EmptyPasswordResponseModel.class);

        String expectedError = LOGIN_EMPTY_FIELD_ERROR;
        String actualError = loginResponse.password().get(0);
        assertThat(actualError).isEqualTo(expectedError);
    });
    }

    @Test
    @DisplayName("Ошибка при попытке входа в систему с незаполненными username и паролем")
    public void emptyUserAndPasswordRegistrationTest() {
        LoginBodyModel loginData = new LoginBodyModel("", "");

        step("Отправка запроса login с незаполненными username и паролем и проверка ответа (400, текст с ошибкой)", () -> {
            EmptyUserAndPasswordResponseModel loginResponse = given(baseRequestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(emptyUserAndPasswordLoginResponseSpec)
                    .extract().as(EmptyUserAndPasswordResponseModel.class);

            String expectedUsernameError = LOGIN_EMPTY_FIELD_ERROR;
            String expectedPasswordError = LOGIN_EMPTY_FIELD_ERROR;
            String actualUsernameError = loginResponse.username().get(0);
            String actualPasswordError = loginResponse.password().get(0);
            assertThat(actualUsernameError).isEqualTo(expectedUsernameError);
            assertThat(actualPasswordError).isEqualTo(expectedPasswordError);
        });
    }


    //---------------------------------------ТЕСТЫ ДО ОПТИМИЗАЦИИ--------------------------------------------
//    @Test
//    public void successfulLoginTest() {
//        LoginBodyModel loginData = new LoginBodyModel(username, password);
//
//        SuccessfulLoginResponseModel loginResponse = given()
//                .log().all()
//                .contentType(JSON)
//                .body(loginData)
//                .basePath("/api/v1")
//                .when()
//                .post("/auth/token/")
//                .then()
//                .log().all()
//                .statusCode(200)
//                .body(matchesJsonSchemaInClasspath("schemas/login/successful_login_response_schema.json"))
//                .body("access", notNullValue())
//                .body("refresh", notNullValue())
//                .extract().as(SuccessfulLoginResponseModel.class);
//
//        String expectedTokenPath = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
//        String actualAccess = loginResponse.access();
//        String actualRefresh = loginResponse.refresh();
//        assertThat(actualAccess).startsWith(expectedTokenPath);
//        assertThat(actualRefresh).startsWith(expectedTokenPath);
//        assertThat(actualAccess).isNotEqualTo(actualRefresh);
//    }
//
//    @Test
//    public void wrongCredentialsLoginTest() {
//        LoginBodyModel loginData = new LoginBodyModel(username, wrongPassword);
//
//        WrongCredentialsLoginResponseModel loginResponse = given()
//                .log().all()
//                .contentType(JSON)
//                .body(loginData)
//                .basePath("/api/v1")
//                .when()
//                .post("/auth/token/")
//                .then()
//                .log().all()
//                .statusCode(401)
//                .body(matchesJsonSchemaInClasspath(
//                        "schemas/login/wrong_credentials_login_response_schema.json"))
//                .body("detail", notNullValue())
//                .extract().as(WrongCredentialsLoginResponseModel.class);
//
//        String expectedDetailError = "Invalid username or password.";
//        String actualDetailError = loginResponse.detail();
//        assertThat(actualDetailError).isEqualTo(expectedDetailError);
//    }


}
