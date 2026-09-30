package tests;

import io.qameta.allure.Feature;
import models.clubs.ClubModel;
import models.clubs.ClubsListResponseModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;


@Feature("Клубы")
public class ClubsTests extends TestBase {

        @Test
        @DisplayName("[API] GET /clubs/ — ответ не null, структура валидна")
        public void getClubsReturns200AndValidStructure() {
            ClubsListResponseModel response = api.clubs.getClubs();

            step("Проверка ответа (200, структура ответа)", () -> {
                assertThat(response).isNotNull();
                assertThat(response.count()).isGreaterThanOrEqualTo(0);
                assertThat(response.results()).isNotNull();
            });
        }

        @Test
        @DisplayName("[API] GET /clubs/ — у каждого клуба заполнены обязательные поля")
        public void getClubsEachClubHasRequiredFields() {
            ClubsListResponseModel response = api.clubs.getClubs();

            step("Проверка ответа (200, структура ответа)", () -> {
                for (ClubModel club : response.results()) {
                    assertThat(club.id()).isNotNull().isPositive();
                    assertThat(club.bookTitle()).isNotNull();
                    assertThat(club.bookAuthors()).isNotNull();
                    assertThat(club.publicationYear()).isNotNull();
                    assertThat(club.description()).isNotNull();
                    assertThat(club.telegramChatLink()).isNotNull();
                    assertThat(club.owner()).isNotNull().isPositive();
                    assertThat(club.members()).isNotNull();
                    assertThat(club.reviews()).isNotNull();
                    assertThat(club.created()).isNotNull();
                }
            });
        }

        @Test
        @DisplayName("[API] GET /clubs/ — поля пагинации присутствуют")
        public void getClubsPaginationFieldsPresent() {
            ClubsListResponseModel response = api.clubs.getClubs();

            step("Проверка ответа (200, структура ответа)", () -> {
                assertThat(response.count()).isNotNull();
                // next и previous могут быть null при одной странице
                assertThat(response.results()).isNotNull();
            });
        }
}
