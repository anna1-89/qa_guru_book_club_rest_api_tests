package api;

import io.qameta.allure.Step;
import models.clubs.*;
import models.login.LoginBodyModel;

import static io.restassured.RestAssured.given;
import static specs.BaseSpec.baseRequestSpec;
import static specs.clubs.ClubsSpec.*;
import static specs.login.LoginSpec.successfulLoginResponseSpec;

public class ClubsApiClient {

    @Step("[API] Отправка запроса на получение списка клубов GET /clubs/")
    public ClubsListResponseModel getClubs() {
        return given(baseRequestSpec)
                .when()
                .get("/clubs/")
                .then()
                .spec(successfulClubsListResponseSpec)
                .extract()
                .as(ClubsListResponseModel.class);
    }

    @Step("[API] Отправка запроса на создание клуба POST /clubs/")
    public ClubModel createClub(String accessToken, CreateClubBodyModel body) {
        return given(baseRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body(body)
                .when()
                .post("/clubs/")
                .then()
                .spec(successfulCreateClubResponseSpec)
                .extract()
                .as(ClubModel.class);
    }

    @Step("[API] Отправка запроса на создание клуба POST /clubs/ и получение id клуба")
    public Integer createClubAndGetId(String accessToken, CreateClubBodyModel body) {
        return given(baseRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body(body)
                .when()
                .post("/clubs/")
                .then()
                .spec(successfulCreateClubResponseSpec)
                .extract()
                .path("id");
    }


    @Step("[API] Отправка запроса на создание клуба без авторизации POST /clubs/")
    public UnauthorizedUserCreateClubResponseModel createClubWithoutAuthorization(CreateClubBodyModel body) {
        return given(baseRequestSpec)
                .body(body)
                .when()
                .post("/clubs/")
                .then()
                .spec(unauthorizedCreateClubResponseSpec)
                .extract()
                .as(UnauthorizedUserCreateClubResponseModel.class);
    }

    @Step("[API] Отправка запроса на создание клуба POST /clubs/")
    public CreateClubWithBlankFieldsResponseModel createClubWithBlankFields(String accessToken, CreateClubBodyModel body) {
        return given(baseRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body(body)
                .when()
                .post("/clubs/")
                .then()
                .spec(blankFieldsCreateClubResponseSpec)
                .extract()
                .as(CreateClubWithBlankFieldsResponseModel.class);
    }

    @Step("[API] Отправка запроса на получение информации по клубу по id GET /clubs/{id}/")
    public ClubModel getClub(Integer clubId) {
        return given(baseRequestSpec)
                .pathParam("id", clubId)
                .when()
                .get("/clubs/{id}/")
                .then()
                .spec(successfulGetClubResponseSpec)
                .extract()
                .as(ClubModel.class);
    }

    @Step("[API] Отправка запроса на получение информации по несуществующему клубу по id GET /clubs/{id}/")
    public NonExistingClubResponseModel getNonExistingClub(Integer clubId) {
        return given(baseRequestSpec)
                .pathParam("id", clubId)
                .when()
                .get("/clubs/{id}/")
                .then()
                .spec(nonExistingClubResponseSpec)
                .extract()
                .as(NonExistingClubResponseModel.class);
    }

    @Step("[API] Отправка запроса на обновление bookTitle клуба PATCH /clubs/")
    public ClubModel updateBookTitleClub(String accessToken, Integer clubId, UpdateBookTitleClubBodyModel body) {
        return given(baseRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .pathParam("id", clubId)
                .body(body)
                .when()
                .patch("/clubs/{id}/")
                .then()
                .spec(successfulUpdateClubResponseSpec)
                .extract()
                .as(ClubModel.class);
    }

    @Step("[API] Отправка запроса на обновление клуба без bookTitle PATCH /clubs/")
    public UpdateBookTitleWithBlankDataClubResponseModel updateClubWithoutBookTitle(String accessToken, Integer clubId, UpdateBookTitleClubBodyModel body) {
        return given(baseRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .pathParam("id", clubId)
                .body(body)
                .when()
                .patch("/clubs/{id}/")
                .then()
                .spec(blankBookTitleUpdateClubResponseSpec)
                .extract()
                .as(UpdateBookTitleWithBlankDataClubResponseModel.class);
    }

    @Step("[API] Отправка запроса на удаление клуба PATCH /clubs/")
    public void deleteBookClub(String accessToken, Integer clubId) {
        given(baseRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .pathParam("id", clubId)
                .when()
                .delete("/clubs/{id}/")
                .then()
                .spec(successfulDeleteClubResponseSpec);
    }


}
