package tests;

import models.login.LoginBodyModel;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseModel;
import models.updateUser.*;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.BaseSpec.baseRequestSpec;
import static specs.login.LoginSpec.successfulLoginResponseSpec;
import static specs.registration.RegistrationSpec.successfulRegistrationResponseSpec;
import static specs.updateUser.UpdateUserSpec.*;
import static tests.TestData.*;

public class UpdateUserTests extends TestBase{
    String username;
    String password;
    String newUsername;
    String newFirstName;
    String newLastName;
    String newEmail;

    @BeforeEach
    public void prepareTestData() {
        Faker faker = new Faker();
        username = faker.name().firstName() + "_" + System.currentTimeMillis();
        password = faker.name().firstName();
        newUsername = faker.name().firstName() + "_" + System.currentTimeMillis();
        newFirstName = faker.name().firstName();
        newLastName = faker.name().lastName();
        newEmail = faker.internet().emailAddress();
    }

    @Test
    @DisplayName("Успешное обновление всех данных пользователя")
    public void successfulUpdateUserTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        step("Отправка запроса registration с данными пользователя", () -> {
                    SuccessfulRegistrationResponseModel registrationResponse = given(baseRequestSpec)
                            .body(registrationData)
                            .when()
                            .post("/users/register/")
                            .then()
                            .spec(successfulRegistrationResponseSpec)
                            .extract().as(SuccessfulRegistrationResponseModel.class);

                    assertThat(registrationResponse.username()).isEqualTo(username);

                });

        LoginBodyModel loginData = new LoginBodyModel(username, password);

        String accessToken = step("Авторизация и получение токена", () ->
            given(baseRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract().path("access"));

        step("Отправка запроса put с новыми данными пользователя и проверка ответа (201)", () -> {
            UpdateAllBodyModel updateUserData = new UpdateAllBodyModel(newUsername, newFirstName, newLastName, newEmail);

            SuccessfulUserUpdateResponseModel userUpdateResponse = given(baseRequestSpec)
                    .auth()
                    .oauth2(accessToken)
                    .body(updateUserData)
                    .when()
                    .put("/users/me/")
                    .then()
                    .spec(successfulUpdateUserResponseSpec)
                    .extract().as(SuccessfulUserUpdateResponseModel.class);

            assertThat(userUpdateResponse.id()).isGreaterThan(0);
            assertThat(userUpdateResponse.username()).isEqualTo(newUsername);
            assertThat(userUpdateResponse.firstName()).isEqualTo(newFirstName);
            assertThat(userUpdateResponse.lastName()).isEqualTo(newLastName);
            assertThat(userUpdateResponse.email()).isEqualTo(newEmail);
            assertThat(userUpdateResponse.remoteAddr()).matches(REGISTRATION_IP_REGEXP);
        });
    }

    @Test
    @DisplayName("Ошибка при попытке обновления данных неавторизованного пользователя")
    public void unauthorizedUserUpdateTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        step("Отправка запроса registration с данными пользователя", () -> {
                    SuccessfulRegistrationResponseModel registrationResponse = given(baseRequestSpec)
                            .body(registrationData)
                            .when()
                            .post("/users/register/")
                            .then()
                            .spec(successfulRegistrationResponseSpec)
                            .extract().as(SuccessfulRegistrationResponseModel.class);

                    assertThat(registrationResponse.username()).isEqualTo(username);
                });

        step("Отправка запроса put с новыми данными пользователя без токена и проверка ответа (401, текст с ошибкой)", () -> {
            UpdateAllBodyModel updateUserData = new UpdateAllBodyModel(newUsername, newFirstName, newLastName, newEmail);

            UnauthorizedUserUpdateResponseModel userUpdateResponse = given(baseRequestSpec)
                    .body(updateUserData)
                    .when()
                    .put("/users/me/")
                    .then()
                    .spec(unauthorizedUpdateUserResponseSpec)
                    .extract().as(UnauthorizedUserUpdateResponseModel.class);

            String expectedError = UPDATE_USER_WRONG_CREDENTIALS_ERROR;
            String actualError = userUpdateResponse.detail();
            assertThat(actualError).isEqualTo(expectedError);
        });
    }

    @Test
    @DisplayName("Ошибка при попытке обновления данных пользователя с существующим username")
    public void existingUserUpdateUserTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        step("Отправка запроса registration с данными пользователя", () -> {
                    SuccessfulRegistrationResponseModel registrationResponse = given(baseRequestSpec)
                            .body(registrationData)
                            .when()
                            .post("/users/register/")
                            .then()
                            .spec(successfulRegistrationResponseSpec)
                            .extract().as(SuccessfulRegistrationResponseModel.class);

                    assertThat(registrationResponse.username()).isEqualTo(username);
                });

        LoginBodyModel loginData = new LoginBodyModel(username, password);
        String accessToken = step("Авторизация и получение токена", () ->
                given(baseRequestSpec)
                        .body(loginData)
                        .when()
                        .post("/auth/token/")
                        .then()
                        .spec(successfulLoginResponseSpec)
                        .extract().path("access"));

        step("Отправка запроса put с новыми данными пользователя c существующим username и проверка ответа (400, текст с ошибкой)", () -> {
            UpdateAllBodyModel updateUserData = new UpdateAllBodyModel(LOGIN_USERNAME, newFirstName, newLastName, newEmail);

            ExistingUserUpdateResponseModel userUpdateResponse = given(baseRequestSpec)
                    .auth()
                    .oauth2(accessToken)
                    .body(updateUserData)
                    .when()
                    .put("/users/me/")
                    .then()
                    .spec(existingUserUpdateUserResponseSpec)
                    .extract().as(ExistingUserUpdateResponseModel.class);

            String expectedError = UPDATE_USER_EXISTING_USER_ERROR;
            String actualError = userUpdateResponse.username().get(0);
            assertThat(actualError).isEqualTo(expectedError);
        });
    }

    @Test
    @DisplayName("Ошибка при попытке обновления данных пользователя с незаполненным username")
    public void blankUsernameUserUpdateUserTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        step("Отправка запроса registration с данными пользователя", () -> {
            SuccessfulRegistrationResponseModel registrationResponse = given(baseRequestSpec)
                    .body(registrationData)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(successfulRegistrationResponseSpec)
                    .extract().as(SuccessfulRegistrationResponseModel.class);

            assertThat(registrationResponse.username()).isEqualTo(username);
        });

        LoginBodyModel loginData = new LoginBodyModel(username, password);
        String accessToken = step("Авторизация и получение токена", () ->
                given(baseRequestSpec)
                        .body(loginData)
                        .when()
                        .post("/auth/token/")
                        .then()
                        .spec(successfulLoginResponseSpec)
                        .extract().path("access"));

        step("Отправка запроса put с новыми данными пользователя c незаполненным username и проверка ответа (400, текст с ошибкой)", () -> {

            UpdateAllBodyModel updateUserData = new UpdateAllBodyModel("", newFirstName, newLastName, newEmail);

            BlankUsernameUserUpdateResponseModel userUpdateResponse = given(baseRequestSpec)
                    .auth()
                    .oauth2(accessToken)
                    .body(updateUserData)
                    .when()
                    .put("/users/me/")
                    .then()
                    .spec(blankUsernameUpdateUserResponseSpec)
                    .extract().as(BlankUsernameUserUpdateResponseModel.class);

            String expectedError = UPDATE_USER_BLANK_FIELD_ERROR;
            String actualError = userUpdateResponse.username().get(0);
            assertThat(actualError).isEqualTo(expectedError);
        });
    }

    @Test
    @DisplayName("Успешное обновление username пользователя")
    public void successfulUpdateUserUsernameTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        step("Отправка запроса registration с данными пользователя", () -> {
            SuccessfulRegistrationResponseModel registrationResponse = given(baseRequestSpec)
                    .body(registrationData)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(successfulRegistrationResponseSpec)
                    .extract().as(SuccessfulRegistrationResponseModel.class);

            assertThat(registrationResponse.username()).isEqualTo(username);
        });

        LoginBodyModel loginData = new LoginBodyModel(username, password);
        String accessToken = step("Авторизация и получение токена", () ->
                given(baseRequestSpec)
                        .body(loginData)
                        .when()
                        .post("/auth/token/")
                        .then()
                        .spec(successfulLoginResponseSpec)
                        .extract().path("access"));

        step("Отправка запроса patch с новым username пользователя и проверка ответа (200)", () -> {
            UpdateUsernameBodyModel updateUserData = new UpdateUsernameBodyModel(newUsername);

            SuccessfulUserUpdateResponseModel userUpdateResponse = given(baseRequestSpec)
                    .auth()
                    .oauth2(accessToken)
                    .body(updateUserData)
                    .when()
                    .patch("/users/me/")
                    .then()
                    .spec(successfulUpdateUserResponseSpec)
                    .extract().as(SuccessfulUserUpdateResponseModel.class);

            assertThat(userUpdateResponse.id()).isGreaterThan(0);
            assertThat(userUpdateResponse.username()).isEqualTo(newUsername);
            assertThat(userUpdateResponse.firstName()).isEqualTo("");
            assertThat(userUpdateResponse.lastName()).isEqualTo("");
            assertThat(userUpdateResponse.email()).isEqualTo("");
            assertThat(userUpdateResponse.remoteAddr()).matches(REGISTRATION_IP_REGEXP);
        });
    }

    @Test
    @DisplayName("Ошибка при попытке обновления username пользователя для неавторизованного пользователя")
    public void unauthorizedUpdateUserUsernameTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        step("Отправка запроса registration с данными пользователя", () -> {
                    SuccessfulRegistrationResponseModel registrationResponse = given(baseRequestSpec)
                            .body(registrationData)
                            .when()
                            .post("/users/register/")
                            .then()
                            .spec(successfulRegistrationResponseSpec)
                            .extract().as(SuccessfulRegistrationResponseModel.class);

                    assertThat(registrationResponse.username()).isEqualTo(username);
                });

        step("Отправка запроса patch с новым username пользователя для неавторизованного пользователя и проверка ответа (401, текст с ошибкой)", () -> {
            UpdateUsernameBodyModel updateUserData = new UpdateUsernameBodyModel(newUsername);

            UnauthorizedUserUpdateResponseModel userUpdateResponse = given(baseRequestSpec)
                    .body(updateUserData)
                    .when()
                    .patch("/users/me/")
                    .then()
                    .spec(unauthorizedUpdateUserResponseSpec)
                    .extract().as(UnauthorizedUserUpdateResponseModel.class);

            String expectedError = UPDATE_USER_WRONG_CREDENTIALS_ERROR;
            String actualError = userUpdateResponse.detail();
            assertThat(actualError).isEqualTo(expectedError);
        });
    }

    @Test
    @DisplayName("Успешное обновление firstName пользователя")
    public void successfulUpdateUserFirstNameTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        step("Отправка запроса registration с данными пользователя", () -> {
            SuccessfulRegistrationResponseModel registrationResponse = given(baseRequestSpec)
                    .body(registrationData)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(successfulRegistrationResponseSpec)
                    .extract().as(SuccessfulRegistrationResponseModel.class);

            assertThat(registrationResponse.username()).isEqualTo(username);
        });

        LoginBodyModel loginData = new LoginBodyModel(username, password);
        String accessToken = step("Авторизация и получение токена", () ->
                given(baseRequestSpec)
                        .body(loginData)
                        .when()
                        .post("/auth/token/")
                        .then()
                        .spec(successfulLoginResponseSpec)
                        .extract().path("access"));

        step("Отправка запроса patch с новым firstName пользователя и проверка ответа (200)", () -> {

            UpdateUserFirstNameBodyModel updateUserData = new UpdateUserFirstNameBodyModel(newFirstName);

            SuccessfulUserUpdateResponseModel userUpdateResponse = given(baseRequestSpec)
                    .auth()
                    .oauth2(accessToken)
                    .body(updateUserData)
                    .when()
                    .patch("/users/me/")
                    .then()
                    .spec(successfulUpdateUserResponseSpec)
                    .extract().as(SuccessfulUserUpdateResponseModel.class);

            assertThat(userUpdateResponse.id()).isGreaterThan(0);
            assertThat(userUpdateResponse.username()).isEqualTo(username);
            assertThat(userUpdateResponse.firstName()).isEqualTo(newFirstName);
            assertThat(userUpdateResponse.lastName()).isEqualTo("");
            assertThat(userUpdateResponse.email()).isEqualTo("");
            assertThat(userUpdateResponse.remoteAddr()).matches(REGISTRATION_IP_REGEXP);
        });
    }

    @Test
    @DisplayName("Успешное обновление lastName пользователя")
    public void successfulUpdateUserLastNameTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        step("Отправка запроса registration с данными пользователя", () -> {
            SuccessfulRegistrationResponseModel registrationResponse = given(baseRequestSpec)
                    .body(registrationData)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(successfulRegistrationResponseSpec)
                    .extract().as(SuccessfulRegistrationResponseModel.class);

            assertThat(registrationResponse.username()).isEqualTo(username);
        });

        LoginBodyModel loginData = new LoginBodyModel(username, password);
        String accessToken = step("Авторизация и получение токена", () ->
                given(baseRequestSpec)
                        .body(loginData)
                        .when()
                        .post("/auth/token/")
                        .then()
                        .spec(successfulLoginResponseSpec)
                        .extract().path("access"));

        step("Отправка запроса patch с новым lastName пользователя и проверка ответа (200)", () -> {

            UpdateUserLastNameBodyModel updateUserData = new UpdateUserLastNameBodyModel(newLastName);

            SuccessfulUserUpdateResponseModel userUpdateResponse = given(baseRequestSpec)
                    .auth()
                    .oauth2(accessToken)
                    .body(updateUserData)
                    .when()
                    .patch("/users/me/")
                    .then()
                    .spec(successfulUpdateUserResponseSpec)
                    .extract().as(SuccessfulUserUpdateResponseModel.class);

            assertThat(userUpdateResponse.id()).isGreaterThan(0);
            assertThat(userUpdateResponse.username()).isEqualTo(username);
            assertThat(userUpdateResponse.firstName()).isEqualTo("");
            assertThat(userUpdateResponse.lastName()).isEqualTo(newLastName);
            assertThat(userUpdateResponse.email()).isEqualTo("");
            assertThat(userUpdateResponse.remoteAddr()).matches(REGISTRATION_IP_REGEXP);
        });
    }

    @Test
    @DisplayName("Успешное обновление email пользователя")
    public void successfulUpdateUserEmailTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        step("Отправка запроса registration с данными пользователя", () -> {
            SuccessfulRegistrationResponseModel registrationResponse = given(baseRequestSpec)
                    .body(registrationData)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(successfulRegistrationResponseSpec)
                    .extract().as(SuccessfulRegistrationResponseModel.class);

            assertThat(registrationResponse.username()).isEqualTo(username);
        });

        LoginBodyModel loginData = new LoginBodyModel(username, password);
        String accessToken = step("Авторизация и получение токена", () ->
                given(baseRequestSpec)
                        .body(loginData)
                        .when()
                        .post("/auth/token/")
                        .then()
                        .spec(successfulLoginResponseSpec)
                        .extract().path("access"));

        step("Отправка запроса patch с новым email пользователя и проверка ответа (200)", () -> {

            UpdateUserEmailBodyModel updateUserData = new UpdateUserEmailBodyModel(newEmail);

            SuccessfulUserUpdateResponseModel userUpdateResponse = given(baseRequestSpec)
                    .auth()
                    .oauth2(accessToken)
                    .body(updateUserData)
                    .when()
                    .patch("/users/me/")
                    .then()
                    .spec(successfulUpdateUserResponseSpec)
                    .extract().as(SuccessfulUserUpdateResponseModel.class);

            assertThat(userUpdateResponse.id()).isGreaterThan(0);
            assertThat(userUpdateResponse.username()).isEqualTo(username);
            assertThat(userUpdateResponse.firstName()).isEqualTo("");
            assertThat(userUpdateResponse.lastName()).isEqualTo("");
            assertThat(userUpdateResponse.email()).isEqualTo(newEmail);
            assertThat(userUpdateResponse.remoteAddr()).matches(REGISTRATION_IP_REGEXP);
        });
    }


}
