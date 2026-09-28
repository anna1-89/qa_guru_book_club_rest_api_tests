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
import static org.assertj.core.api.Assertions.assertThat;
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
        SuccessfulRegistrationResponseModel registrationResponse = api.users.register(registrationData);

        step("Проверка прохождения регистрации", () ->
            assertThat(registrationResponse.username()).isEqualTo(username));

        LoginBodyModel loginData = new LoginBodyModel(username, password);
        String accessToken = api.auth.loginAndGetAccessToken(loginData);

        UpdateAllBodyModel updateUserData = new UpdateAllBodyModel(newUsername, newFirstName, newLastName, newEmail);
        SuccessfulUserUpdateResponseModel userUpdateResponse = api.users.updateAllUserData(updateUserData, accessToken);

        step("Проверка ответа (201, соответствие данным)", () -> {
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
        SuccessfulRegistrationResponseModel registrationResponse = api.users.register(registrationData);

        step("Проверка прохождения регистрации", () ->
                assertThat(registrationResponse.username()).isEqualTo(username));

       UpdateAllBodyModel updateUserData = new UpdateAllBodyModel(newUsername, newFirstName, newLastName, newEmail);
       UnauthorizedUserUpdateResponseModel userUpdateResponse = api.users.updateAllUserDataWithoutLogin(updateUserData);

        step("Проверка ответа (401, текст с ошибкой)", () -> {
            String expectedError = UPDATE_USER_WRONG_CREDENTIALS_ERROR;
            String actualError = userUpdateResponse.detail();
            assertThat(actualError).isEqualTo(expectedError);
        });
    }

    @Test
    @DisplayName("Ошибка при попытке обновления данных пользователя с существующим username")
    public void existingUserUpdateUserTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
        SuccessfulRegistrationResponseModel registrationResponse = api.users.register(registrationData);

        step("Проверка прохождения регистрации", () ->
            assertThat(registrationResponse.username()).isEqualTo(username));

        LoginBodyModel loginData = new LoginBodyModel(username, password);
        String accessToken = api.auth.loginAndGetAccessToken(loginData);

        UpdateAllBodyModel updateUserData = new UpdateAllBodyModel(LOGIN_USERNAME, newFirstName, newLastName, newEmail);
        ExistingUserUpdateResponseModel userUpdateResponse = api.users.updateAllUserDataWithPresentUsername(updateUserData, accessToken);

        step("Проверка ответа (400, текст с ошибкой)", () -> {
            String expectedError = UPDATE_USER_EXISTING_USER_ERROR;
            String actualError = userUpdateResponse.username().get(0);
            assertThat(actualError).isEqualTo(expectedError);
        });
    }

    @Test
    @DisplayName("Ошибка при попытке обновления данных пользователя с незаполненным username")
    public void blankUsernameUserUpdateUserTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
        SuccessfulRegistrationResponseModel registrationResponse = api.users.register(registrationData);

        step("Проверка прохождения регистрации", () ->
                assertThat(registrationResponse.username()).isEqualTo(username));

        LoginBodyModel loginData = new LoginBodyModel(username, password);
        String accessToken = api.auth.loginAndGetAccessToken(loginData);

        UpdateAllBodyModel updateUserData = new UpdateAllBodyModel("", newFirstName, newLastName, newEmail);
        BlankUsernameUserUpdateResponseModel userUpdateResponse = api.users.updateAllUserDataWithoutUsername(updateUserData, accessToken);

        step("Проверка ответа (400, текст с ошибкой)", () -> {
            String expectedError = UPDATE_USER_BLANK_FIELD_ERROR;
            String actualError = userUpdateResponse.username().get(0);
            assertThat(actualError).isEqualTo(expectedError);
        });
    }

    @Test
    @DisplayName("Успешное обновление username пользователя")
    public void successfulUpdateUserUsernameTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
        SuccessfulRegistrationResponseModel registrationResponse = api.users.register(registrationData);

        step("Проверка прохождения регистрации", () ->
                assertThat(registrationResponse.username()).isEqualTo(username));

        LoginBodyModel loginData = new LoginBodyModel(username, password);
        String accessToken = api.auth.loginAndGetAccessToken(loginData);

        UpdateUsernameBodyModel updateUserData = new UpdateUsernameBodyModel(newUsername);
        SuccessfulUserUpdateResponseModel userUpdateResponse = api.users.updateUserUsername(updateUserData, accessToken);

        step("Проверка ответа (200, соответствие данных)", () -> {
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
        SuccessfulRegistrationResponseModel registrationResponse = api.users.register(registrationData);

        step("Проверка прохождения регистрации", () ->
                assertThat(registrationResponse.username()).isEqualTo(username));

        UpdateUsernameBodyModel updateUserData = new UpdateUsernameBodyModel(newUsername);
        UnauthorizedUserUpdateResponseModel userUpdateResponse = api.users.updateUserUsernameWithoutLogin(updateUserData);

        step("Проверка ответа (401, текст с ошибкой)", () -> {
            String expectedError = UPDATE_USER_WRONG_CREDENTIALS_ERROR;
            String actualError = userUpdateResponse.detail();
            assertThat(actualError).isEqualTo(expectedError);
        });
    }

    @Test
    @DisplayName("Успешное обновление firstName пользователя")
    public void successfulUpdateUserFirstNameTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
        SuccessfulRegistrationResponseModel registrationResponse = api.users.register(registrationData);

        step("Проверка прохождения регистрации", () ->
                assertThat(registrationResponse.username()).isEqualTo(username));

        LoginBodyModel loginData = new LoginBodyModel(username, password);
        String accessToken = api.auth.loginAndGetAccessToken(loginData);

        UpdateUserFirstNameBodyModel updateUserData = new UpdateUserFirstNameBodyModel(newFirstName);
        SuccessfulUserUpdateResponseModel userUpdateResponse = api.users.updateUserFirstName(updateUserData, accessToken);

        step("Проверка ответа (200, соответствие данных)", () -> {
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
        SuccessfulRegistrationResponseModel registrationResponse = api.users.register(registrationData);

        step("Проверка прохождения регистрации", () ->
                assertThat(registrationResponse.username()).isEqualTo(username));

        LoginBodyModel loginData = new LoginBodyModel(username, password);
        String accessToken = api.auth.loginAndGetAccessToken(loginData);

        UpdateUserLastNameBodyModel updateUserData = new UpdateUserLastNameBodyModel(newLastName);
        SuccessfulUserUpdateResponseModel userUpdateResponse = api.users.updateUserLastName(updateUserData, accessToken);

        step("Проверка ответа (200, соответствие данных)", () -> {
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
        SuccessfulRegistrationResponseModel registrationResponse = api.users.register(registrationData);

        step("Проверка прохождения регистрации", () ->
                assertThat(registrationResponse.username()).isEqualTo(username));

        LoginBodyModel loginData = new LoginBodyModel(username, password);
        String accessToken = api.auth.loginAndGetAccessToken(loginData);

        UpdateUserEmailBodyModel updateUserData = new UpdateUserEmailBodyModel(newEmail);
        SuccessfulUserUpdateResponseModel userUpdateResponse = api.users.updateUserEmail(updateUserData, accessToken);

        step("Проверка ответа (200, соответствие данных)", () -> {

            assertThat(userUpdateResponse.id()).isGreaterThan(0);
            assertThat(userUpdateResponse.username()).isEqualTo(username);
            assertThat(userUpdateResponse.firstName()).isEqualTo("");
            assertThat(userUpdateResponse.lastName()).isEqualTo("");
            assertThat(userUpdateResponse.email()).isEqualTo(newEmail);
            assertThat(userUpdateResponse.remoteAddr()).matches(REGISTRATION_IP_REGEXP);
        });
    }


}
