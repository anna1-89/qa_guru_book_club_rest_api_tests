package tests.ui;

import models.clubs.CreateClubBodyModel;
import models.localstorage.LocalStorageAuthModel;
import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseModel;
import models.reviews.ReviewBodyModel;
import models.reviews.ReviewResponseModel;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tests.TestBase;

import static com.codeborne.selenide.Selenide.localStorage;
import static com.codeborne.selenide.Selenide.open;
import static io.qameta.allure.Allure.step;

public class ReviewTests extends TestBase {

    String username;
    String password;

    String bookTitle;
    String bookAuthors;
    Integer publicationYear;
    String description;
    String telegramChatLink;

    String review;
    Integer assessment;
    Integer readPages;

    @BeforeEach
    public void prepareTestData() {
        Faker faker = new Faker();
        username = faker.name().firstName() + System.currentTimeMillis();
        password = "12345";

        bookTitle = "API Club QA" + faker.book().title();
        bookAuthors = faker.book().author();
        publicationYear = 2020;
        description = faker.lorem().sentence();
        telegramChatLink = "https://t.me/qa_guru_" + faker.internet().uuid();

        review = faker.lorem().sentence();
        assessment = faker.number().numberBetween(1, 5);
        readPages = faker.number().numberBetween(1, 200);
    }

    @Test
    @DisplayName("[API + UI] Создание отзыва")
    public void successfulCreateReview(){

        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
        SuccessfulRegistrationResponseModel registrationResponse = api.users.register(registrationData);

        LoginBodyModel loginData = new LoginBodyModel(username, password);
        SuccessfulLoginResponseModel loginResponse = api.auth.login(loginData);

        String accessToken = loginResponse.access();
        String refreshToken = loginResponse.refresh();

        String localStorageAuthBody = new LocalStorageAuthModel(registrationResponse, accessToken, refreshToken, true).toJson();

        CreateClubBodyModel createClubBody = new CreateClubBodyModel(
                bookTitle,
                bookAuthors,
                publicationYear,
                description,
                telegramChatLink
        );
        Integer createdClubId = api.clubs.createClubAndGetId(accessToken, createClubBody);

        open("/favicon.ico");
        localStorage().setItem("book_club_auth", localStorageAuthBody);

        clubPage.openClubPage(createdClubId);
        clubPage.checkPageClub();

        clubPage.clickAddReviewButton();
        step("Заполняем форму данными для отзыва)", () -> {
            clubPage.enterAssessment(assessment);
            clubPage.enterReadPages(readPages);
            clubPage.enterReview(review);
        });
        clubPage.clickSaveReviewButton();
        step("Проверяем добавление отзыва и данные)", () -> {
            clubPage.checkExistReview(review);
            clubPage.checkCreatedReviewData(readPages,review);
        });
    }

    @Test
    @DisplayName("[API + UI] Удаление отзыва")
    public void successfulDeleteReview(){

        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
        SuccessfulRegistrationResponseModel registrationResponse = api.users.register(registrationData);

        LoginBodyModel loginData = new LoginBodyModel(username, password);
        SuccessfulLoginResponseModel loginResponse = api.auth.login(loginData);

        String accessToken = loginResponse.access();
        String refreshToken = loginResponse.refresh();

        String localStorageAuthBody = new LocalStorageAuthModel(registrationResponse, accessToken, refreshToken, true).toJson();

        CreateClubBodyModel createClubBody = new CreateClubBodyModel(
                bookTitle,
                bookAuthors,
                publicationYear,
                description,
                telegramChatLink
        );
        Integer createdClubId = api.clubs.createClubAndGetId(accessToken, createClubBody);

        ReviewBodyModel reviewBody = new ReviewBodyModel(
                createdClubId,
                review,
                assessment,
                readPages
        );
        ReviewResponseModel createdReview = api.reviews.createReview(accessToken,reviewBody);

        open("/favicon.ico");
        localStorage().setItem("book_club_auth", localStorageAuthBody);

        clubPage.openClubPage(createdClubId);
        clubPage.checkPageClub();

        clubPage.checkExistReview(review);

        clubPage.clickDeleteReviewButton();
        clubPage.checkDeletedReview(review);
    }

}
