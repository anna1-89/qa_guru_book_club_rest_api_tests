package api;

import io.qameta.allure.Step;
import models.login.*;
import models.logout.EmptyTokenLogoutResponseModel;
import models.logout.InvalidTokenLogoutResponseModel;
import models.logout.LogoutBodyModel;
import models.logout.SuccessfulLogoutResponseModel;

import static io.restassured.RestAssured.given;
import static specs.BaseSpec.baseRequestSpec;
import static specs.login.LoginSpec.*;
import static specs.logout.LogoutSpec.*;

public class AuthApiClient {

    @Step("[API] Отправка запроса на авторизацию /auth/token/ с корректными данными")
    public SuccessfulLoginResponseModel login(LoginBodyModel loginBody) {
        return given(baseRequestSpec)
                .body(loginBody)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract()
                .as(SuccessfulLoginResponseModel.class);
    }

    @Step("[API] Отправка запроса на авторизацию /auth/token/ и получение refresh токена")
    public String loginAndGetRefreshToken(LoginBodyModel loginBody) {
        return given(baseRequestSpec)
                .body(loginBody)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract()
                .path("refresh");
    }

    @Step("[API] Отправка запроса на авторизацию /auth/token/ и получение access-токена")
    public String loginAndGetAccessToken(LoginBodyModel loginBody) {
        return given(baseRequestSpec)
                .body(loginBody)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract()
                .path("access");
    }

    @Step("[API] Отправка запроса на авторизацию /auth/token/ с невалидными данными")
    public WrongCredentialsLoginResponseModel loginWrongCredentials(LoginBodyModel loginBody) {
        return given(baseRequestSpec)
                .body(loginBody)
                .when()
                .post("/auth/token/")
                .then()
                .spec(wrongCredentialsLoginResponseSpec)
                .extract()
                .as(WrongCredentialsLoginResponseModel.class);
    }

    @Step("[API] Отправка запроса на авторизацию /auth/token/ без заполненного username")
    public EmptyUsernameResponseModel loginWithEmptyUsername(LoginBodyModel loginBody) {
        return given(baseRequestSpec)
                .body(loginBody)
                .when()
                .post("/auth/token/")
                .then()
                .spec(emptyUsernameLoginResponseSpec)
                .extract().as(EmptyUsernameResponseModel.class);
    }

    @Step("[API] Отправка запроса на авторизацию /auth/token/ без заполненного password")
    public EmptyPasswordResponseModel loginWithEmptyPassword(LoginBodyModel loginBody) {
        return given(baseRequestSpec)
                .body(loginBody)
                .when()
                .post("/auth/token/")
                .then()
                .spec(emptyPasswordLoginResponseSpec)
                .extract().as(EmptyPasswordResponseModel.class);
    }

    @Step("[API] Отправка запроса на авторизацию /auth/token/ без заполненных username и password")
    public EmptyUserAndPasswordResponseModel loginWithEmptyUsernameAndPassword(LoginBodyModel loginBody) {
        return given(baseRequestSpec)
                .body(loginBody)
                .when()
                .post("/auth/token/")
                .then()
                .spec(emptyUsernameAndPasswordLoginResponseSpec)
                .extract().as(EmptyUserAndPasswordResponseModel.class);
    }

    @Step("[API] Отправка запроса на выход из системы /auth/logout/")
    public SuccessfulLogoutResponseModel logout(LogoutBodyModel logoutBody) {
        return given(baseRequestSpec)
                .body(logoutBody)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(successfulLogoutResponseSpec)
                .extract()
                .as(SuccessfulLogoutResponseModel.class);
    }

    @Step("[API] Отправка запроса на выход из системы /auth/logout/ с невалидным токеном")
    public InvalidTokenLogoutResponseModel logoutInvalidToken(LogoutBodyModel logoutBody) {
        return given(baseRequestSpec)
                .body(logoutBody)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(invalidTokenLogoutResponseSpec)
                .extract().as(InvalidTokenLogoutResponseModel.class);
    }

    @Step("[API] Отправка запроса на выход из системы /auth/logout/ с пустым токеном")
    public EmptyTokenLogoutResponseModel logoutEmptyToken(LogoutBodyModel logoutBody) {
        return given(baseRequestSpec)
                .body(logoutBody)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(emptyTokenLogoutResponseSpec)
                .extract().as(EmptyTokenLogoutResponseModel.class);
    }

    @Step("[API] Отправка запроса на выход из системы /auth/logout/ с уже использованным токеном")
    public InvalidTokenLogoutResponseModel logoutBlacklistedToken(LogoutBodyModel logoutBody) {
        return given(baseRequestSpec)
                .body(logoutBody)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(invalidTokenLogoutResponseSpec)
                .extract().as(InvalidTokenLogoutResponseModel.class);
    }



}
