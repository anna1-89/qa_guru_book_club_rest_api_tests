package tests;

import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.login.WrongCredentialsLoginResponseModel;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.BaseSpec.baseRequestSpec;
import static specs.login.LoginSpec.*;
import static tests.TestData.*;

public class LoginTests extends TestBase {

    @Test
    public void successfulLoginTest() {
        LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD);

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
    }

    @Test
    public void wrongCredentialsLoginTest() {
        LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, LOGIN_WRONG_PASSWORD);

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
