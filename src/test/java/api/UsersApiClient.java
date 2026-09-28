package api;

import io.qameta.allure.Step;
import models.registration.*;
import models.updateUser.*;

import static io.restassured.RestAssured.given;
import static specs.BaseSpec.baseRequestSpec;
import static specs.registration.RegistrationSpec.*;
import static specs.updateUser.UpdateUserSpec.*;

public class UsersApiClient {

    @Step("Отправка запроса на регистрацию /users/register/ с корректными данными")
    public SuccessfulRegistrationResponseModel register(RegistrationBodyModel registrationBody) {
        return given(baseRequestSpec)
                .body(registrationBody)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                .extract().as(SuccessfulRegistrationResponseModel.class);
    }

    @Step("Отправка запроса на регистрацию /users/register/ c данными существующего пользователя")
    public ExistingUserResponseModel registerExistingUser(RegistrationBodyModel registrationBody) {
        return given(baseRequestSpec)
                .body(registrationBody)
                .when()
                .post("/users/register/")
                .then()
                .spec(existingUserRegistrationResponseSpec)
                .extract().as(ExistingUserResponseModel.class);
    }

    @Step("Отправка запроса на регистрацию /users/register/ с пустым username")
    public EmptyUsernameResponseModel registerWithEmptyUsername(RegistrationBodyModel registrationBody) {
        return given(baseRequestSpec)
                .body(registrationBody)
                .when()
                .post("/users/register/")
                .then()
                .spec(emptyUsernameRegistrationResponseSpec)
                .extract().as(EmptyUsernameResponseModel.class);
    }

    @Step("Отправка запроса на регистрацию /users/register/ с пустым password")
    public EmptyPasswordResponseModel registerWithEmptyPassword(RegistrationBodyModel registrationBody) {
        return given(baseRequestSpec)
                .body(registrationBody)
                .when()
                .post("/users/register/")
                .then()
                .spec(emptyPasswordRegistrationResponseSpec)
                .extract().as(EmptyPasswordResponseModel.class);
    }

    @Step("Отправка запроса на регистрацию /users/register/ с пустыми username и password")
    public EmptyUsernameAndPasswordResponseModel registerWithEmptyUsernameAndPassword(RegistrationBodyModel registrationBody) {
        return given(baseRequestSpec)
                .body(registrationBody)
                .when()
                .post("/users/register/")
                .then()
                .spec(emptyUsernameAndPasswordRegistrationResponseSpec)
                .extract().as(EmptyUsernameAndPasswordResponseModel.class);
    }

    @Step("Отправка запроса на обновление всех данных пользователя /users/me/")
    public SuccessfulUserUpdateResponseModel updateAllUserData(UpdateAllBodyModel updateUserBody, String accessToken) {
        return given(baseRequestSpec)
                .auth()
                .oauth2(accessToken)
                .body(updateUserBody)
                .when()
                .put("/users/me/")
                .then()
                .spec(successfulUpdateUserResponseSpec)
                .extract().as(SuccessfulUserUpdateResponseModel.class);
    }

    @Step("Отправка запроса на обновление всех данных пользователя /users/me/ для неавторизованного пользователя")
    public UnauthorizedUserUpdateResponseModel updateAllUserDataWithoutLogin(UpdateAllBodyModel updateUserBody) {
        return given(baseRequestSpec)
                .body(updateUserBody)
                .when()
                .put("/users/me/")
                .then()
                .spec(unauthorizedUpdateUserResponseSpec)
                .extract().as(UnauthorizedUserUpdateResponseModel.class);
    }

    @Step("Отправка запроса на обновление всех данных пользователя /users/me/ с уже существующим username")
    public ExistingUserUpdateResponseModel updateAllUserDataWithPresentUsername(UpdateAllBodyModel updateUserBody, String accessToken) {
        return given(baseRequestSpec)
                .auth()
                .oauth2(accessToken)
                .body(updateUserBody)
                .when()
                .put("/users/me/")
                .then()
                .spec(existingUserUpdateUserResponseSpec)
                .extract().as(ExistingUserUpdateResponseModel.class);
    }

    @Step("Отправка запроса на обновление всех данных пользователя /users/me/ с незаполненным username")
    public BlankUsernameUserUpdateResponseModel updateAllUserDataWithoutUsername(UpdateAllBodyModel updateUserBody, String accessToken) {
        return given(baseRequestSpec)
                .auth()
                .oauth2(accessToken)
                .body(updateUserBody)
                .when()
                .put("/users/me/")
                .then()
                .spec(blankUsernameUpdateUserResponseSpec)
                .extract().as(BlankUsernameUserUpdateResponseModel.class);
    }

    @Step("Отправка запроса на обновление только username пользователя /users/me/")
    public SuccessfulUserUpdateResponseModel updateUserUsername(UpdateUsernameBodyModel updateUserBody, String accessToken) {
        return given(baseRequestSpec)
                .auth()
                .oauth2(accessToken)
                .body(updateUserBody)
                .when()
                .patch("/users/me/")
                .then()
                .spec(successfulUpdateUserResponseSpec)
                .extract().as(SuccessfulUserUpdateResponseModel.class);
    }

    @Step("Отправка запроса на обновление только username пользователя /users/me/ для неавторизованного пользователя")
    public UnauthorizedUserUpdateResponseModel updateUserUsernameWithoutLogin(UpdateUsernameBodyModel updateUserBody) {
        return given(baseRequestSpec)
                .body(updateUserBody)
                .when()
                .patch("/users/me/")
                .then()
                .spec(unauthorizedUpdateUserResponseSpec)
                .extract().as(UnauthorizedUserUpdateResponseModel.class);
    }

    @Step("Отправка запроса на обновление только fisrtName пользователя /users/me/")
    public SuccessfulUserUpdateResponseModel updateUserFirstName(UpdateUserFirstNameBodyModel updateUserBody, String accessToken) {
        return given(baseRequestSpec)
                .auth()
                .oauth2(accessToken)
                .body(updateUserBody)
                .when()
                .patch("/users/me/")
                .then()
                .spec(successfulUpdateUserResponseSpec)
                .extract().as(SuccessfulUserUpdateResponseModel.class);
    }

    @Step("Отправка запроса на обновление только lastName пользователя /users/me/")
    public SuccessfulUserUpdateResponseModel updateUserLastName(UpdateUserLastNameBodyModel updateUserBody, String accessToken) {
        return given(baseRequestSpec)
                .auth()
                .oauth2(accessToken)
                .body(updateUserBody)
                .when()
                .patch("/users/me/")
                .then()
                .spec(successfulUpdateUserResponseSpec)
                .extract().as(SuccessfulUserUpdateResponseModel.class);
    }

    @Step("Отправка запроса на обновление только email пользователя /users/me/")
    public SuccessfulUserUpdateResponseModel updateUserEmail(UpdateUserEmailBodyModel updateUserBody, String accessToken) {
        return given(baseRequestSpec)
                .auth()
                .oauth2(accessToken)
                .body(updateUserBody)
                .when()
                .patch("/users/me/")
                .then()
                .spec(successfulUpdateUserResponseSpec)
                .extract().as(SuccessfulUserUpdateResponseModel.class);
    }

}
