package pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class CreateClubPage {

    //Elements
    private SelenideElement bookTitleField = $("#bookTitle");
    private SelenideElement bookAuthorsField = $("#bookAuthors");
    private SelenideElement publicationYearField = $("#publicationYear");
    private SelenideElement descriptionField = $("#description");
    private SelenideElement telegramChatLinkField = $("#telegramChatLink");
    private SelenideElement submitButton = $(".submit-btn");

    //Actions
    @Step("Открываем форму создания нового ккуба")
    public CreateClubPage openCreateClubPage() {
        open("/clubs/create");
        return this;
    }

    @Step("Заполняем bookTitle")
    public CreateClubPage enterBookTitle(String value) {
        bookTitleField.setValue(value);
        return this;
    }

    @Step("Заполняем bookAuthors")
    public CreateClubPage enterBookAuthors(String value) {
        bookAuthorsField.setValue(value);
        return this;
    }

    @Step("Заполняем publicationYear")
    public CreateClubPage enterPublicationYear(String value) {
        publicationYearField.setValue(value);
        return this;
    }

    @Step("Заполняем description")
    public CreateClubPage enterDescription(String value) {
        descriptionField.setValue(value);
        return this;
    }

    @Step("Заполняем telegramChatLink")
    public CreateClubPage enterTelegramChatLink(String value) {
        telegramChatLinkField.setValue(value);
        return this;
    }

    @Step("Нажимаем кнопку \"Создать клуб\"")
    public CreateClubPage clickSubmitButton () {
        submitButton.click();
        return this;
    }

}
