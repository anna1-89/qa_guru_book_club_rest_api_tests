package pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class MainPage {

    // Elements
    private SelenideElement createClubButton = $("[data-testid=create-club-link]");
    private SelenideElement searchInput = $(".search-input");
    private SelenideElement previewClubCard = $(".club-card");
    private SelenideElement previewClubCardTitle = $(".club-card");
    private SelenideElement previewClubCardPublishingYear = $(".year");
    private SelenideElement previewClubCardAuthors = $(".authors");
    private SelenideElement previewClubCardDescription = $(".description");



    // Actions
    @Step("Открываем страницу с профилем")
    public MainPage openMainPage() {
        open("");
        return this;
    }

    @Step("Открываем страницу с созданием нового клуба")
    public MainPage openCreateNewClubPage() {
        createClubButton.click();
        return this;
    }

    @Step("Вводим запрос на поиск клуба")
    public MainPage searchClub(String value) {
        searchInput.setValue(value).pressEnter();
        return this;
    }

    @Step("Проверяем, что клуб создан")
    public MainPage checkExistClub(String value) {
        previewClubCard.$(byText(value)).should(exist);
        return this;
    }

    @Step("Проверяем, что данные созданного клуба соответствуют заданным")
    public MainPage checkCreatedClubData(
            String bookTitle,
            String publishingYear,
            String bookAuthors,
            String bookDescription
    ) {
        previewClubCardTitle.shouldHave(text(bookTitle));
        previewClubCardPublishingYear.shouldHave(text(publishingYear.toString()));
        previewClubCardAuthors.shouldHave(text(bookAuthors));
        previewClubCardDescription.shouldHave(text(bookDescription));
        return this;
    }
}
