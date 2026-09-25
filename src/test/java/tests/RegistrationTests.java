package tests;

import models.registration.*;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    public void successfulRegistrationTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

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
    }

    @Test
    public void existingUserWrongRegistrationTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        SuccessfulRegistrationResponseModel firstRegistrationResponse = given(baseRequestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                .extract().as(SuccessfulRegistrationResponseModel.class);

        assertThat(firstRegistrationResponse.username()).isEqualTo(username);

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
    }

        @Test
        public void emptyUserWrongRegistrationTest() {
            RegistrationBodyModel registrationData = new RegistrationBodyModel("", password);

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
    }

    @Test
    public void emptyPasswordWrongRegistrationTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, "");

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
    }

    @Test
    public void emptyUserAndPasswordWrongRegistrationTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel("", "");

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
    }

}
