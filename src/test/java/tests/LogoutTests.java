package tests;

import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static specs.login.LoginSpec.loginRequestSpec;
import static specs.login.LoginSpec.successfulLoginResponseSpec;

public class LogoutTests extends TestBase {

    String username = "qaguru";
    String password = "qaguru123";

    @Test
    public void successfulLogoutTest() {
        LoginBodyModel loginData = new LoginBodyModel(username, password);

        String refreshToken = given(loginRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract().path("refresh");

        //todo move to models and specs
        String logoutData = "{\"refresh\": \"" + refreshToken + "\"}";

        given()
                .log().all()
                .contentType(JSON)
                .body(logoutData)
//                .formParam("refresh", refreshToken)
                .basePath("/api/v1")
                .when()
                .post("/auth/logout/")
                .then()
                .log().all()
                .statusCode(200);

        //todo check logoutResponse is empty

        //todo add more negative tests

    }
}
