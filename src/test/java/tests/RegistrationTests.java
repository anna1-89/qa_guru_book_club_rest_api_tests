package tests;

import models.registration.*;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static tests.TestData.*;

public class RegistrationTests extends TestBase {

    String username;
    String password;

    @BeforeEach
    public void prepareTestData() {
        Faker faker = new Faker();
        username = faker.name().firstName() + System.currentTimeMillis();
        password = faker.name().firstName();
    }

    @Test
    @DisplayName("Успешная регистрация")
    public void successfulRegistrationTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        SuccessfulRegistrationResponseModel registrationResponse = api.users.register(registrationData);

        step("Проверка ответа (201, соответствие входным данным)", () -> {
            assertThat(registrationResponse.id()).isGreaterThan(0);
            assertThat(registrationResponse.username()).isEqualTo(username);
            assertThat(registrationResponse.firstName()).isEqualTo("");
            assertThat(registrationResponse.lastName()).isEqualTo("");
            assertThat(registrationResponse.email()).isEqualTo("");

            assertThat(registrationResponse.remoteAddr()).matches(REGISTRATION_IP_REGEXP);
        });
    }

    @Test
    @DisplayName("Ошибка регистрации для существующего пользователя")
    public void existingUserWrongRegistrationTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        SuccessfulRegistrationResponseModel firstRegistrationResponse = api.users.register(registrationData);

        step("Проверка ответа (201, соответствие входным данным", () -> {
            assertThat(firstRegistrationResponse.username()).isEqualTo(username);
        });

        ExistingUserResponseModel secondRegistrationResponse = api.users.registerExistingUser(registrationData);

        step("Проверка ответа (400, текст с ошибкой)", () -> {
            String expectedError = REGISTRATION_EXISTING_USER_ERROR;
            String actualError = secondRegistrationResponse.username().get(0);
            assertThat(actualError).isEqualTo(expectedError);
        });
    }

        @Test
        @DisplayName("Ошибка при попытке регистрации пользователя с пустым username")
        public void emptyUserWrongRegistrationTest() {
            RegistrationBodyModel registrationData = new RegistrationBodyModel("", password);

            EmptyUsernameResponseModel registrationResponse = api.users.registerWithEmptyUsername(registrationData);

            step("Отправка запроса registration с пустым username и проверка ответа (400, текст с ошибкой)", () -> {
                String expectedError = REGISTRATION_EMPTY_FIELD_ERROR;
                String actualError = registrationResponse.username().get(0);
                assertThat(actualError).isEqualTo(expectedError);
            });
    }

    @Test
    @DisplayName("Ошибка при попытке регистрации пользователя с пустым паролем")
    public void emptyPasswordWrongRegistrationTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, "");

        EmptyPasswordResponseModel registrationResponse = api.users.registerWithEmptyPassword(registrationData);

        step("Проверка ответа (400, текст с ошибкой)", () -> {
            String expectedError = REGISTRATION_EMPTY_FIELD_ERROR;
            String actualError = registrationResponse.password().get(0);
            assertThat(actualError).isEqualTo(expectedError);
        });
    }

    @Test
    @DisplayName("Ошибка при попытке регистрации пользователя с пустыми username и паролем")
    public void emptyUserAndPasswordWrongRegistrationTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel("", "");

        EmptyUsernameAndPasswordResponseModel registrationResponse = api.users.registerWithEmptyUsernameAndPassword(registrationData);

        step("Проверка ответа (400, текст с ошибкой)", () -> {
            String expectedUsernameError = REGISTRATION_EMPTY_FIELD_ERROR;
            String expectedPasswordError = REGISTRATION_EMPTY_FIELD_ERROR;
            String actualUsernameError = registrationResponse.username().get(0);
            String actualPasswordError = registrationResponse.password().get(0);
            assertThat(actualUsernameError).isEqualTo(expectedUsernameError);
            assertThat(actualPasswordError).isEqualTo(expectedPasswordError);
        });
    }

}
