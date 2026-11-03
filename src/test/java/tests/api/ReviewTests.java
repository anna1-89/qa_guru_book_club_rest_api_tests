package tests.api;

import models.clubs.CreateClubBodyModel;
import models.login.LoginBodyModel;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseModel;
import models.reviews.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static tests.TestData.*;

public class ReviewTests extends TestBase {

    @Test
    @DisplayName("[API] Создание review")
    public void successfulCreateReview() {
        LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD);
        String accessToken = api.auth.loginAndGetAccessToken(loginData);

        CreateClubBodyModel createClubBody = new CreateClubBodyModel(
                faker.book().title() + System.currentTimeMillis(),
                faker.book().author(),
                faker.number().numberBetween(2010,2026),
                faker.lorem().sentence(),
                "https://t.me/qa_guru_" + faker.internet().uuid()
        );

        Integer createdClubId = api.clubs.createClubAndGetId(accessToken, createClubBody);

        ReviewBodyModel reviewBody = new ReviewBodyModel(
                createdClubId,
                faker.lorem().sentence(),
                faker.number().numberBetween(1, 5),
                faker.number().numberBetween(1, 200)
        );
        ReviewResponseModel createdReview = api.reviews.createReview(accessToken,reviewBody);

        step("Проверка ответа (201, соответствие данных)", () -> {
            assertThat(createdReview.club()).isEqualTo(createdClubId);
            assertThat(createdReview.review()).isEqualTo(reviewBody.review());
            assertThat(createdReview.assessment()).isEqualTo(reviewBody.assessment());
            assertThat(createdReview.readPages()).isEqualTo(reviewBody.readPages());
            assertThat(createdReview.created()).isNotNull();
            assertThat(createdReview.user()).isNotNull();
            assertThat(createdReview.user().username()).isEqualTo(LOGIN_USERNAME);
        });
    }

    @Test
    @DisplayName("[API] Создание review неавторизованным пользователем")
    public void unauthorizedUserCreateReview() {
        LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD);
        String accessToken = api.auth.loginAndGetAccessToken(loginData);

        CreateClubBodyModel createClubBody = new CreateClubBodyModel(
                faker.book().title() + System.currentTimeMillis(),
                faker.book().author(),
                faker.number().numberBetween(2010,2026),
                faker.lorem().sentence(),
                "https://t.me/qa_guru_" + faker.internet().uuid()
        );

        Integer createdClubId = api.clubs.createClubAndGetId(accessToken, createClubBody);

        ReviewBodyModel reviewBody = new ReviewBodyModel(
                createdClubId,
                faker.lorem().sentence(),
                faker.number().numberBetween(1, 5),
                faker.number().numberBetween(1, 200)
        );

        UnauthorizedUserReviewResponseModel createdReview = api.reviews.createReviewWithoutAuthorization(reviewBody);

        step("Проверка ответа (401, текст с ошибкой)", () -> {
            String expectedError = REVIEW_WRONG_CREDENTIALS_ERROR;
            String actualError = createdReview.detail();
            assertThat(actualError).isEqualTo(expectedError);
        });
    }

    @Test
    @DisplayName("[API] Создание review c пустыми полями")
    public void сreateReviewWithBlankFields() {
        LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD);
        String accessToken = api.auth.loginAndGetAccessToken(loginData);

        CreateClubBodyModel createClubBody = new CreateClubBodyModel(
                faker.book().title() + System.currentTimeMillis(),
                faker.book().author(),
                faker.number().numberBetween(2010,2026),
                faker.lorem().sentence(),
                "https://t.me/qa_guru_" + faker.internet().uuid()
        );

        Integer createdClubId = api.clubs.createClubAndGetId(accessToken, createClubBody);

        ReviewBodyModel reviewBody = new ReviewBodyModel(
                createdClubId,
                "",
                null,
                null
        );

        BlankFieldsReviewResponseModel createdReview = api.reviews.createReviewWithBlankFields(accessToken,reviewBody);

        step("Проверка ответа (400, текст с ошибками)", () -> {
            String expectedError = REVIEW_BLANK_FIELDS_ERROR;
            String expectedNullError = REVIEW_NULL_FIELDS_ERROR;
            String actualReviewError = createdReview.review().get(0);
            String actualAssessmentError = createdReview.assessment().get(0);
            String actualReadPagesError = createdReview.readPages().get(0);
            assertThat(actualReviewError).isEqualTo(expectedError);
            assertThat(actualAssessmentError).isEqualTo(expectedNullError);
            assertThat(actualReadPagesError).isEqualTo(expectedNullError);
        });
    }

    @Test
    @DisplayName("[API] Получение конкретного review")
    public void successfulGetReview() {
        LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD);
        String accessToken = api.auth.loginAndGetAccessToken(loginData);

        CreateClubBodyModel createClubBody = new CreateClubBodyModel(
                faker.book().title() + System.currentTimeMillis(),
                faker.book().author(),
                faker.number().numberBetween(2010,2026),
                faker.lorem().sentence(),
                "https://t.me/qa_guru_" + faker.internet().uuid()
        );

        Integer createdClubId = api.clubs.createClubAndGetId(accessToken, createClubBody);

        ReviewBodyModel reviewBody = new ReviewBodyModel(
                createdClubId,
                faker.lorem().sentence(),
                faker.number().numberBetween(1, 5),
                faker.number().numberBetween(1, 200)
        );
        Integer createdReviewId = api.reviews.createReviewAndGetId(accessToken,reviewBody);

        ReviewResponseModel createdReview = api.reviews.getReview(createdReviewId);

        step("Проверка ответа (200, соответствие полученных данных по review запрашиваемым данным)", () -> {
            assertThat(createdReview.club()).isEqualTo(createdClubId);
            assertThat(createdReview.review()).isEqualTo(reviewBody.review());
            assertThat(createdReview.assessment()).isEqualTo(reviewBody.assessment());
            assertThat(createdReview.readPages()).isEqualTo(reviewBody.readPages());
            assertThat(createdReview.created()).isNotNull();
            assertThat(createdReview.user()).isNotNull();
            assertThat(createdReview.user().username()).isEqualTo(LOGIN_USERNAME);
        });
    }

    @Test
    @DisplayName("[API] Получение списка review")
    public void successfulGetListReview() {
        LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD);
        String accessToken = api.auth.loginAndGetAccessToken(loginData);

        CreateClubBodyModel createClubBody = new CreateClubBodyModel(
                faker.book().title() + System.currentTimeMillis(),
                faker.book().author(),
                faker.number().numberBetween(2010,2026),
                faker.lorem().sentence(),
                "https://t.me/qa_guru_" + faker.internet().uuid()
        );

        Integer createdClubId = api.clubs.createClubAndGetId(accessToken, createClubBody);

        ReviewBodyModel reviewBody = new ReviewBodyModel(
                createdClubId,
                faker.lorem().sentence(),
                faker.number().numberBetween(1, 5),
                faker.number().numberBetween(1, 200)
        );
        Integer createdReviewId = api.reviews.createReviewAndGetId(accessToken,reviewBody);

        ReviewsListResponseModel reviews = api.reviews.getAllReviews();

        step("Проверка ответа (200, соответствие полученных данных по review запрашиваемым данным)", () -> {
            assertThat(reviews).isNotNull();
            assertThat(reviews.count()).isGreaterThanOrEqualTo(0);
            assertThat(reviews.results()).isNotNull();
        });
    }

    @Test
    @DisplayName("[API] Обновление review PUT")
    public void successfulUpdateReview() {
        LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD);
        String accessToken = api.auth.loginAndGetAccessToken(loginData);

        CreateClubBodyModel createClubBody = new CreateClubBodyModel(
                faker.book().title() + System.currentTimeMillis(),
                faker.book().author(),
                faker.number().numberBetween(2010,2026),
                faker.lorem().sentence(),
                "https://t.me/qa_guru_" + faker.internet().uuid()
        );

        Integer createdClubId = api.clubs.createClubAndGetId(accessToken, createClubBody);

        ReviewBodyModel reviewBody = new ReviewBodyModel(
                createdClubId,
                faker.lorem().sentence(),
                faker.number().numberBetween(1, 5),
                faker.number().numberBetween(1, 200)
        );
        Integer createdReviewId = api.reviews.createReviewAndGetId(accessToken,reviewBody);

        ReviewResponseModel createdReview = api.reviews.getReview(createdReviewId);

        ReviewBodyModel updatedReviewBody = new ReviewBodyModel(
                createdClubId,
                faker.lorem().sentence(),
                faker.number().numberBetween(1, 5),
                faker.number().numberBetween(1, 200)
        );

        ReviewResponseModel updatedReview = api.reviews.updateReview(accessToken, createdReviewId, updatedReviewBody);

        step("Проверка ответа (201, соответствие данных)", () -> {
            assertThat(updatedReview.club()).isEqualTo(createdClubId);
            assertThat(updatedReview.review()).isEqualTo(updatedReviewBody.review());
            assertThat(updatedReview.assessment()).isEqualTo(updatedReviewBody.assessment());
            assertThat(updatedReview.readPages()).isEqualTo(updatedReviewBody.readPages());
            assertThat(updatedReview.created()).isNotNull();
            assertThat(updatedReview.user()).isNotNull();
            assertThat(updatedReview.user().username()).isEqualTo(LOGIN_USERNAME);
            assertThat(updatedReview.modified()).isNotNull();
        });
    }

    @Test
    @DisplayName("[API] Обновление не своего review PUT")
    public void notOwnerUpdateReview() {
        LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD);
        String accessToken = api.auth.loginAndGetAccessToken(loginData);

        CreateClubBodyModel createClubBody = new CreateClubBodyModel(
                faker.book().title() + System.currentTimeMillis(),
                faker.book().author(),
                faker.number().numberBetween(2010,2026),
                faker.lorem().sentence(),
                "https://t.me/qa_guru_" + faker.internet().uuid()
        );

        Integer createdClubId = api.clubs.createClubAndGetId(accessToken, createClubBody);

        ReviewBodyModel reviewBody = new ReviewBodyModel(
                createdClubId,
                faker.lorem().sentence(),
                faker.number().numberBetween(1, 5),
                faker.number().numberBetween(1, 200)
        );
        Integer createdReviewId = api.reviews.createReviewAndGetId(accessToken,reviewBody);

        ReviewBodyModel updatedReviewBody = new ReviewBodyModel(
                createdClubId,
                faker.lorem().sentence(),
                faker.number().numberBetween(1, 5),
                faker.number().numberBetween(1, 200)
        );

        RegistrationBodyModel registrationData = new RegistrationBodyModel(
                faker.name().firstName() + System.currentTimeMillis(),
                "1234");
        SuccessfulRegistrationResponseModel registrationResponse = api.users.register(registrationData);

        LoginBodyModel loginAnotherData = new LoginBodyModel(registrationData.username(), registrationData.password());
        String accessAnotherToken = api.auth.loginAndGetAccessToken(loginAnotherData);

        NotOwnerResponseModel updatedReview = api.reviews.updateNotOwnerReview(accessAnotherToken, createdReviewId, updatedReviewBody);

        step("Проверка ответа (403, текст с ошибкой)", () -> {
            String expectedError = NO_OWNER_ERROR;
            String actualError = updatedReview.detail();
        });
    }

    @Test
    @DisplayName("[API] Удаление review")
    public void successfulDeleteReview() {
        LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD);
        String accessToken = api.auth.loginAndGetAccessToken(loginData);

        CreateClubBodyModel createClubBody = new CreateClubBodyModel(
                faker.book().title() + System.currentTimeMillis(),
                faker.book().author(),
                faker.number().numberBetween(2010,2026),
                faker.lorem().sentence(),
                "https://t.me/qa_guru_" + faker.internet().uuid()
        );

        Integer createdClubId = api.clubs.createClubAndGetId(accessToken, createClubBody);

        ReviewBodyModel reviewBody = new ReviewBodyModel(
                createdClubId,
                faker.lorem().sentence(),
                faker.number().numberBetween(1, 5),
                faker.number().numberBetween(1, 200)
        );
        Integer createdReviewId = api.reviews.createReviewAndGetId(accessToken,reviewBody);

        api.reviews.deleteReview(accessToken, createdReviewId);
    }

    @Test
    @DisplayName("[API] Удаление не своего review")
    public void notOwnerDeleteReview() {
        LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD);
        String accessToken = api.auth.loginAndGetAccessToken(loginData);

        CreateClubBodyModel createClubBody = new CreateClubBodyModel(
                faker.book().title() + System.currentTimeMillis(),
                faker.book().author(),
                faker.number().numberBetween(2010,2026),
                faker.lorem().sentence(),
                "https://t.me/qa_guru_" + faker.internet().uuid()
        );

        Integer createdClubId = api.clubs.createClubAndGetId(accessToken, createClubBody);

        ReviewBodyModel reviewBody = new ReviewBodyModel(
                createdClubId,
                faker.lorem().sentence(),
                faker.number().numberBetween(1, 5),
                faker.number().numberBetween(1, 200)
        );
        Integer createdReviewId = api.reviews.createReviewAndGetId(accessToken,reviewBody);

        RegistrationBodyModel registrationData = new RegistrationBodyModel(
                faker.name().firstName() + System.currentTimeMillis(),
                "1234");
        SuccessfulRegistrationResponseModel registrationResponse = api.users.register(registrationData);

        LoginBodyModel loginAnotherData = new LoginBodyModel(registrationData.username(), registrationData.password());
        String accessAnotherToken = api.auth.loginAndGetAccessToken(loginAnotherData);

        NotOwnerResponseModel deletedReview = api.reviews.deleteNotOwnerReview(accessAnotherToken, createdReviewId);

        step("Проверка ответа (403, текст с ошибкой)", () -> {
            String expectedError = NO_OWNER_ERROR;
            String actualError = deletedReview.detail();
        });
    }


}
