package tests;

import io.qameta.allure.Feature;
import models.clubs.*;
import models.login.LoginBodyModel;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static tests.TestData.*;

@Feature("Клубы")
    public class ClubTests extends TestBase {

        String bookTitle;
        String newBookTitle;
        String bookAuthors;
        Integer publicationYear;
        String description;
        String telegramChatLink;

        @BeforeEach
        public void prepareTestData() {
            Faker faker = new Faker();
            bookTitle = "API Club " + faker.book().title();
            newBookTitle = "API Club " + faker.book().title();
            bookAuthors = faker.book().author();
            publicationYear = 2020;
            description = faker.lorem().sentence();
            telegramChatLink = "https://t.me/qa_guru_" + faker.internet().uuid();
        }

        @Test
        @DisplayName("[API] Создание клуба")
        public void successfulCreateClub() {
            LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD);
            String accessToken = api.auth.loginAndGetAccessToken(loginData);

            CreateClubBodyModel createClubBody = new CreateClubBodyModel(
                    bookTitle,
                    bookAuthors,
                    publicationYear,
                    description,
                    telegramChatLink
            );
            ClubModel createdClubResponse = api.clubs.createClub(accessToken, createClubBody);

            step("Проверка ответа (201, соответствие данных)", () -> {
                assertThat(createdClubResponse.bookTitle()).isEqualTo(bookTitle);
                assertThat(createdClubResponse.bookAuthors()).isEqualTo(bookAuthors);
                assertThat(createdClubResponse.publicationYear()).isEqualTo(publicationYear);
                assertThat(createdClubResponse.description()).isEqualTo(description);
                assertThat(createdClubResponse.telegramChatLink()).isEqualTo(telegramChatLink);
            });
        }

    @Test
    @DisplayName("[API] Создание клуба неавторизованным пользователем")
    public void createClubWithoutAuthorization() {
        CreateClubBodyModel createClubBody = new CreateClubBodyModel(
                bookTitle,
                bookAuthors,
                publicationYear,
                description,
                telegramChatLink
        );
        UnauthorizedUserCreateClubResponseModel createdClubResponse = api.clubs.createClubWithoutAuthorization(createClubBody);

        step("Проверка ответа (401, текст с ошибкой)", () -> {
            String expectedError = CREATE_CLUB_WRONG_CREDENTIALS_ERROR;
            String actualError = createdClubResponse.detail();
            assertThat(actualError).isEqualTo(expectedError);
        });
    }

    @Test
    @DisplayName("[API] Создание клуба с пустыми полями")
    public void createClubWithBlankFields() {
        LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD);
        String accessToken = api.auth.loginAndGetAccessToken(loginData);

        CreateClubBodyModel createClubBody = new CreateClubBodyModel(
                "",
                "",
                null,
                "",
                ""
        );
        CreateClubWithBlankFieldsResponseModel createdClubResponse = api.clubs.createClubWithBlankFields(accessToken, createClubBody);

        step("Проверка ответа (400, текст с ошибками)", () -> {
            String expectedError = CREATE_CLUB_BLANK_FIELDS_ERROR;
            String expectedPublicationYearError = CREATE_CLUB_BLANK_PUBLICATION_FIELD_ERROR ;
            String actualBookTitleError = createdClubResponse.bookTitle().get(0);
            String actualPublicationYear = createdClubResponse.publicationYear().get(0);
            String actualDescription = createdClubResponse.description().get(0);
            String actualTelegramChatLink = createdClubResponse.telegramChatLink().get(0);
            assertThat(actualBookTitleError).isEqualTo(expectedError);
            assertThat(actualPublicationYear).isEqualTo(expectedPublicationYearError);
            assertThat(actualDescription).isEqualTo(expectedError);
            assertThat(actualTelegramChatLink).isEqualTo(expectedError);
        });
    }

    @Test
    @DisplayName("[API] Получение информации по клубу")
    public void successfulGetClub() {
        LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD);
        String accessToken = api.auth.loginAndGetAccessToken(loginData);

        CreateClubBodyModel createClubBody = new CreateClubBodyModel(
                bookTitle,
                bookAuthors,
                publicationYear,
                description,
                telegramChatLink
        );

        Integer createdClubId = api.clubs.createClubAndGetId(accessToken, createClubBody);

        ClubModel createdClubResponse = api.clubs.getClub(createdClubId);

        step("Проверка ответа (200, соответствие полученных данных по клубу запрашиваемым данным)", () -> {
            assertThat(createdClubResponse.id()).isEqualTo(createdClubId);
            assertThat(createdClubResponse.bookTitle()).isEqualTo(bookTitle);
            assertThat(createdClubResponse.bookAuthors()).isEqualTo(bookAuthors);
            assertThat(createdClubResponse.publicationYear()).isEqualTo(publicationYear);
            assertThat(createdClubResponse.description()).isEqualTo(description);
            assertThat(createdClubResponse.telegramChatLink()).isEqualTo(telegramChatLink);
        });
    }

    @Test
    @DisplayName("[API] Получение информации по несуществющему клубу")
    public void getNonExistingClub() {
        NonExistingClubResponseModel clubResponse = api.clubs.getNonExistingClub(NON_EXISTING_CLUB_ID);

        step("Проверка ответа (404, текст с ошибкой)", () -> {
            String expectedError = NON_EXISTING_CLUB_ERROR;
            String actualError = clubResponse.detail();
            assertThat(actualError).isEqualTo(expectedError);
        });
    }

    @Test
    @DisplayName("[API] Обновление bookTitle клуба")
    public void successfulUpdateBookTitleClub() {
        LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD);
        String accessToken = api.auth.loginAndGetAccessToken(loginData);

        CreateClubBodyModel createClubBody = new CreateClubBodyModel(
                bookTitle,
                bookAuthors,
                publicationYear,
                description,
                telegramChatLink
        );

        Integer createdClubId = api.clubs.createClubAndGetId(accessToken, createClubBody);

        UpdateBookTitleClubBodyModel updateClubBody = new UpdateBookTitleClubBodyModel(
                newBookTitle
        );

        ClubModel updateClubResponse = api.clubs.updateBookTitleClub(accessToken,createdClubId,updateClubBody);

        step("Проверка ответа (200, соответствие полученных данных по клубу запрашиваемым данным)", () -> {
            assertThat(updateClubResponse.id()).isEqualTo(createdClubId);
            assertThat(updateClubResponse.bookTitle()).isEqualTo(newBookTitle);
            assertThat(updateClubResponse.bookAuthors()).isEqualTo(bookAuthors);
            assertThat(updateClubResponse.publicationYear()).isEqualTo(publicationYear);
            assertThat(updateClubResponse.description()).isEqualTo(description);
            assertThat(updateClubResponse.telegramChatLink()).isEqualTo(telegramChatLink);
        });
    }

    @Test
    @DisplayName("[API] Обновление клуба без bookTitle  ")
    public void blankBookTitleUpdateClub() {
        LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD);
        String accessToken = api.auth.loginAndGetAccessToken(loginData);

        CreateClubBodyModel createClubBody = new CreateClubBodyModel(
                bookTitle,
                bookAuthors,
                publicationYear,
                description,
                telegramChatLink
        );

        Integer createdClubId = api.clubs.createClubAndGetId(accessToken, createClubBody);

        UpdateBookTitleClubBodyModel updateClubBody = new UpdateBookTitleClubBodyModel("");

        UpdateBookTitleWithBlankDataClubResponseModel updateClubResponse = api.clubs.updateClubWithoutBookTitle(accessToken,createdClubId,updateClubBody);

        step("Проверка ответа (400, текст с ошибкой)", () -> {
            String expectedError = UPDATE_CLUB_BLANK_FIELDS_ERROR;
            String actualError = updateClubResponse.bookTitle().get(0);
            assertThat(actualError).isEqualTo(expectedError);
        });
    }

    @Test
    @DisplayName("[API] Удаление клуба")
    public void successfulDeleteClub() {
        LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD);
        String accessToken = api.auth.loginAndGetAccessToken(loginData);

        CreateClubBodyModel createClubBody = new CreateClubBodyModel(
                bookTitle,
                bookAuthors,
                publicationYear,
                description,
                telegramChatLink
        );

        Integer createdClubId = api.clubs.createClubAndGetId(accessToken, createClubBody);

        api.clubs.deleteBookClub(accessToken, createdClubId);

        NonExistingClubResponseModel clubResponse = api.clubs.getNonExistingClub(createdClubId);

        step("Проверка ответа (404, текст с ошибкой)", () -> {
            String expectedError = NON_EXISTING_CLUB_ERROR;
            String actualError = clubResponse.detail();
            assertThat(actualError).isEqualTo(expectedError);
        });
    }



}
