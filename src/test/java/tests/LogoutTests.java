package tests;

import models.login.LoginBodyModel;
import models.logout.EmptyTokenLogoutResponseModel;
import models.logout.InvalidTokenLogoutResponseModel;
import models.logout.LogoutBodyModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static tests.TestData.*;

public class LogoutTests extends TestBase {

    @Test
    @DisplayName("Успешный выход из системы")
    public void successfulLogoutTest() {
        LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD);
        String refreshToken = api.auth.loginAndGetRefreshToken(loginData);

        step("Выход из учетки с refresh-токеном и проверка ответа (200)", () -> {
            LogoutBodyModel logoutData = new LogoutBodyModel(refreshToken);
            api.auth.logout(logoutData);
        });
    }

    @Test
    @DisplayName("Ошибка при попытке выхода из системы без токена")
    public void emptyTokenLogoutTest() {
        LogoutBodyModel logoutData = new LogoutBodyModel("");

        EmptyTokenLogoutResponseModel logoutResponse = api.auth.logoutEmptyToken(logoutData);

        step("Проверка ответа (400, текст с ошибкой)", () -> {
            String expectedError = LOGOUT_EMPTY_TOKEN_ERROR;
            String actualError = logoutResponse.refresh().get(0);
            assertThat(actualError).isEqualTo(expectedError);
        });
    }

    @Test
    @DisplayName("Ошибка при попытке выхода из системы с невалидным токеном")
    public void invalidTokenLogoutTest() {
        LogoutBodyModel logoutData = new LogoutBodyModel(LOGOUT_WRONG_TOKEN);

        InvalidTokenLogoutResponseModel logoutResponse = api.auth.logoutInvalidToken(logoutData);

        step("Проверка ответа (401, текст с ошибкой)", () -> {
            String expectedError = LOGOUT_WRONG_TOKEN_ERROR;
            String expectedCodeError = LOGOUT_WRONG_TOKEN_CODE_ERROR;
            String actualError = logoutResponse.detail();
            String actualCodeError = logoutResponse.code();
            assertThat(actualError).isEqualTo(expectedError);
            assertThat(actualCodeError).isEqualTo(expectedCodeError);
        });
    }

    @Test
    @DisplayName("Ошибка при попытке выхода из системы с уже использованным токеном")
    public void expiredTokenLogoutTest() {
        LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD);
        String refreshToken = api.auth.loginAndGetRefreshToken(loginData);

        step("Проверка успешного первого выхода", () -> {
            LogoutBodyModel logoutData = new LogoutBodyModel(refreshToken);
            api.auth.logout(logoutData);
        });

        LogoutBodyModel logoutData = new LogoutBodyModel(refreshToken);
        InvalidTokenLogoutResponseModel secondLogoutResponse = api.auth.logoutBlacklistedToken(logoutData);

        step("Проверка ответа (401, текст с ошибкой)", () -> {
            String expectedError = LOGOUT_BLACKLISTED_TOKEN_ERROR;
            String expectedCodeError = LOGOUT_WRONG_TOKEN_CODE_ERROR;
            String actualError = secondLogoutResponse.detail();
            String actualCodeError = secondLogoutResponse.code();
            assertThat(actualError).isEqualTo(expectedError);
            assertThat(actualCodeError).isEqualTo(expectedCodeError);
        });
    }




}
