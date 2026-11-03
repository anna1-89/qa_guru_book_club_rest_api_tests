package tests.ui;

import models.clubs.ClubModel;
import models.clubs.CreateClubBodyModel;
import models.localstorage.LocalStorageAuthModel;
import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseModel;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;

public class ClubTests extends TestBase {

    String username;
    String password;

    String bookTitle;
    String bookAuthors;
    Integer publicationYear;
    String description;
    String telegramChatLink;

    @BeforeEach
    public void prepareTestData() {
        Faker faker = new Faker();
        username = faker.name().firstName() + System.currentTimeMillis();
        password = "12345";

        bookTitle = "API Club Qa Test AP" + faker.book().title();
        bookAuthors = faker.book().author();
        publicationYear = 2020;
        description = faker.lorem().sentence();
        telegramChatLink = "https://t.me/qa_guru_" + faker.internet().uuid();
    }

    @Test
    @DisplayName("[API + UI] Создание клуба")
    public void successfulCreateClub(){

        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
        SuccessfulRegistrationResponseModel registrationResponse = api.users.register(registrationData);

        LoginBodyModel loginData = new LoginBodyModel(username, password);
        SuccessfulLoginResponseModel loginResponse = api.auth.login(loginData);

        String accessToken = loginResponse.access();
        String refreshToken = loginResponse.refresh();

        String localStorageAuthBody = new LocalStorageAuthModel(registrationResponse, accessToken, refreshToken, true).toJson();

        open("/favicon.ico");
        localStorage().setItem("book_club_auth", localStorageAuthBody);

        createClubPage.openCreateClubPage();
        step("Заполняем форму данными для нового клуба)", () -> {
            createClubPage.enterBookTitle(bookTitle);
            createClubPage.enterBookAuthors(bookAuthors);
            createClubPage.enterPublicationYear(publicationYear.toString());
            createClubPage.enterDescription(description);
            createClubPage.enterTelegramChatLink(telegramChatLink);
            });

        createClubPage.clickSubmitButton();

        step("Находим созданный клуб, проверяем, что он создался и соответствует заданным данным)", () -> {
            mainPage.searchClub(bookTitle);
            mainPage.checkExistClub(bookTitle);
            mainPage.checkCreatedClubData(bookTitle,publicationYear.toString(),bookAuthors,description);
        });
    }

    @Test
    @DisplayName("[API + UI] Невозможность выхода из клуба его создателю")
    public void cantLeaveClubAsOwnerTest(){
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
        SuccessfulRegistrationResponseModel registrationResponse = api.users.register(registrationData);

        LoginBodyModel loginData = new LoginBodyModel(username, password);
        String accessToken = api.auth.loginAndGetAccessToken(loginData);
        String refreshToken = api.auth.loginAndGetRefreshToken(loginData);

        String localStorageAuthBody = new LocalStorageAuthModel(registrationResponse, accessToken, refreshToken, true).toJson();

        CreateClubBodyModel createClubBody = new CreateClubBodyModel(
                bookTitle,
                bookAuthors,
                publicationYear,
                description,
                telegramChatLink
        );

        ClubModel createdClub = api.clubs.createClub(accessToken, createClubBody);
        Integer clubId = createdClub.id();

        open("/favicon.ico");
        localStorage().setItem("book_club_auth", localStorageAuthBody);

        clubPage.openClubPage(clubId);
        clubPage.checkPageClub();
        clubPage.clickLeaveClubButton();
        clubPage.checkLeaveClubError();
        open("/clubs/" + clubId);
    }

}
