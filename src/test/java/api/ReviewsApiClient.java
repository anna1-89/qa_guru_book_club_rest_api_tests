package api;

import io.qameta.allure.Step;
import models.reviews.*;

import static io.restassured.RestAssured.given;
import static specs.BaseSpec.baseRequestSpec;
import static specs.clubs.ClubsSpec.*;
import static specs.reviews.ReviewsSpec.*;

public class ReviewsApiClient {

    @Step("[API] Отправка запроса на получение списка всех reviews GET /clubs/ GET /clubs/reviews/")
    public ReviewsListResponseModel getAllReviews() {
        return given(baseRequestSpec)
                .when()
                .get("/clubs/reviews/")
                .then()
                .spec(successfulReviewsListResponseSpec)
                .extract()
                .as(ReviewsListResponseModel.class);
    }

    @Step("[API] Отправка запроса на получение конкретного review GET /clubs/reviews/{id}/")
    public ReviewResponseModel getReview (Integer reviewId) {
        return given(baseRequestSpec)
                .pathParam("id", reviewId)
                .when()
                .get("/clubs/reviews/{id}/")
                .then()
                .spec(successfulReviewResponseSpec)
                .extract()
                .as(ReviewResponseModel.class);
    }

    @Step("[API] Отправка запроса на создание review POST /clubs/reviews/")
    public ReviewResponseModel createReview(String accessToken, ReviewBodyModel body) {
        return given(baseRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body(body)
                .when()
                .post("/clubs/reviews/")
                .then()
                .spec(successfulCreateReviewResponseSpec)
                .extract()
                .as(ReviewResponseModel.class);
    }

    @Step("[API] Отправка запроса на создание review POST /clubs/reviews/ и получение id review")
    public Integer createReviewAndGetId(String accessToken, ReviewBodyModel body) {
        return given(baseRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body(body)
                .when()
                .post("/clubs/reviews/")
                .then()
                .spec(successfulCreateReviewResponseSpec)
                .extract()
                .path("id");
    }

    @Step("[API] Отправка запроса на создание review без авторизации POST /clubs/reviews/")
    public UnauthorizedUserReviewResponseModel createReviewWithoutAuthorization(ReviewBodyModel body) {
        return given(baseRequestSpec)
                .body(body)
                .when()
                .post("/clubs/reviews/")
                .then()
                .spec(unauthorizedUserReviewResponseSpec)
                .extract()
                .as(UnauthorizedUserReviewResponseModel.class);
    }

    @Step("[API] Отправка запроса на создание review с незаполненными данными POST /clubs/reviews/")
    public BlankFieldsReviewResponseModel createReviewWithBlankFields(String accessToken, ReviewBodyModel body) {
        return given(baseRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body(body)
                .when()
                .post("/clubs/reviews/")
                .then()
                .spec(blankFieldsReviewResponseSpec)
                .extract()
                .as(BlankFieldsReviewResponseModel.class);
    }

    @Step("[API] Отправка запроса на обновление review PUT /clubs/reviews/{id}/")
    public ReviewResponseModel updateReview(String accessToken, int reviewId, ReviewBodyModel body) {
        return given(baseRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .pathParam("id", reviewId)
                .body(body)
                .when()
                .put("/clubs/reviews/{id}/")
                .then()
                .spec(successfulReviewResponseSpec)
                .extract()
                .as(ReviewResponseModel.class);
    }

    @Step("[API] Отправка запроса на обновление review PUT /clubs/reviews/{id}/")
    public NotOwnerResponseModel updateNotOwnerReview(String accessToken, int reviewId, ReviewBodyModel body) {
        return given(baseRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .pathParam("id", reviewId)
                .body(body)
                .when()
                .put("/clubs/reviews/{id}/")
                .then()
                .spec(notOwnerReviewResponseSpec)
                .extract()
                .as(NotOwnerResponseModel.class);
    }


    @Step("Отправка запроса на удаление review DELETE /clubs/reviews/{id}/")
    public void deleteReview(String accessToken, int reviewId) {
        given(baseRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .pathParam("id", reviewId)
                .when()
                .delete("/clubs/reviews/{id}/")
                .then()
                .spec(successfulDeleteReviewResponseSpec);
    }

    @Step("[API] Отправка запроса на обновление review PUT /clubs/reviews/{id}/")
    public NotOwnerResponseModel deleteNotOwnerReview(String accessToken, int reviewId) {
        return given(baseRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .pathParam("id", reviewId)
                .when()
                .delete("/clubs/reviews/{id}/")
                .then()
                .extract()
                .as(NotOwnerResponseModel.class);
    }

}
