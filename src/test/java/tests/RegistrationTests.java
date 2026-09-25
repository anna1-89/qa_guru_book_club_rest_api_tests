package tests;

import models.registration.*;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.BaseSpec.baseRequestSpec;
import static specs.registration.RegistrationSpec.*;
import static tests.TestData.*;

public class RegistrationTests extends TestBase {

    String username;
    String password;

    @BeforeEach
    public void prepareTestData() {
        Faker faker = new Faker();
        username = faker.name().firstName();
        password = faker.name().firstName();
    }

    @Test
    @DisplayName("Успешная регистрация")
    public void successfulRegistrationTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        step("Отправка запроса registration с корректными данными и проверка ответа (201)", () -> {
            SuccessfulRegistrationResponseModel registrationResponse = given(baseRequestSpec)
                    .body(registrationData)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(successfulRegistrationResponseSpec)
                    .extract().as(SuccessfulRegistrationResponseModel.class);

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

        step("Отправка запроса registration с корректными данными", () -> {
                    SuccessfulRegistrationResponseModel firstRegistrationResponse = given(baseRequestSpec)
                            .body(registrationData)
                            .when()
                            .post("/users/register/")
                            .then()
                            .spec(successfulRegistrationResponseSpec)
                            .extract().as(SuccessfulRegistrationResponseModel.class);

                    assertThat(firstRegistrationResponse.username()).isEqualTo(username);
                });

        step("Отправка повторного запроса registration с теми же регистрационными данные и проверка ответа (400, текст с ошибкой)", () -> {
            ExistingUserResponseModel secondRegistrationResponse = given(baseRequestSpec)
                    .body(registrationData)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(existingUserRegistrationResponseSpec)
                    .extract().as(ExistingUserResponseModel.class);

            String expectedError = REGISTRATION_EXISTING_USER_ERROR;
            String actualError = secondRegistrationResponse.username().get(0);
            assertThat(actualError).isEqualTo(expectedError);
        });
    }

        @Test
        @DisplayName("Ошибка при попытке регистрации пользователя с пустым username")
        public void emptyUserWrongRegistrationTest() {
            RegistrationBodyModel registrationData = new RegistrationBodyModel("", password);

            step("Отправка запроса registration с пустым username и проверка ответа (400, текст с ошибкой)", () -> {
                EmptyUsernameResponseModel registrationResponse = given(baseRequestSpec)
                        .body(registrationData)
                        .when()
                        .post("/users/register/")
                        .then()
                        .spec(emptyUserRegistrationResponseSpec)
                        .extract().as(EmptyUsernameResponseModel.class);

                String expectedError = REGISTRATION_EMPTY_FIELD_ERROR;
                String actualError = registrationResponse.username().get(0);
                assertThat(actualError).isEqualTo(expectedError);
            });
    }

    @Test
    @DisplayName("Ошибка при попытке регистрации пользователя с пустым паролем")
    public void emptyPasswordWrongRegistrationTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, "");

        step("Отправка запроса registration с пустым паролем и проверка ответа (400, текст с ошибкой)", () -> {
            EmptyPasswordResponseModel registrationResponse = given(baseRequestSpec)
                    .body(registrationData)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(emptyPasswordRegistrationResponseSpec)
                    .extract().as(EmptyPasswordResponseModel.class);

            String expectedError = REGISTRATION_EMPTY_FIELD_ERROR;
            String actualError = registrationResponse.password().get(0);
            assertThat(actualError).isEqualTo(expectedError);
        });
    }

    @Test
    @DisplayName("Ошибка при попытке регистрации пользователя с пустыми username и паролем")
    public void emptyUserAndPasswordWrongRegistrationTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel("", "");

        step("Отправка запроса registration с пустыми username и паролем и проверка ответа (400, текст с ошибкой)", () -> {
            EmptyUserAndPasswordResponseModel registrationResponse = given(baseRequestSpec)
                    .body(registrationData)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(emptyUserAndPasswordRegistrationResponseSpec)
                    .extract().as(EmptyUserAndPasswordResponseModel.class);

            String expectedUsernameError = REGISTRATION_EMPTY_FIELD_ERROR;
            String expectedPasswordError = REGISTRATION_EMPTY_FIELD_ERROR;
            String actualUsernameError = registrationResponse.username().get(0);
            String actualPasswordError = registrationResponse.password().get(0);
            assertThat(actualUsernameError).isEqualTo(expectedUsernameError);
            assertThat(actualPasswordError).isEqualTo(expectedPasswordError);
        });
    }

}
