package pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;

public class ClubPage {
    //Elements
    private SelenideElement clubContent = $(".club-content");
    private SelenideElement leaveClubButton = $(".leave-btn");
    private SelenideElement errorMessage = $(".error");

    private SelenideElement addReviewButton = $(".add-review-btn");
    private SelenideElement assessmentField = $("#assessment");
    private SelenideElement readPagesField = $("#readPages");
    private SelenideElement reviewField = $("#review");
    private SelenideElement saveReviewButton = $(".save-btn");
    private SelenideElement deleteReviewButton = $(".delete-review-btn");
    private SelenideElement editReviewButton = $(".edit-review-btn");
    private SelenideElement reviewCardReadPages = $(".read-pages");
    private SelenideElement reviewCardReview = $(".review-content");


    //Actions
    @Step("Открываем страницу созданного клуба")
    public ClubPage openClubPage(Integer clubId) {
        open("/clubs/" + clubId);
        return this;
    }

    @Step("Проверяем, что страница клуба открыта")
    public ClubPage checkPageClub() {
        clubContent.shouldBe(visible);
        return this;
    }

    @Step("Нажимаем на кнопку выхода из клуба")
    public ClubPage clickLeaveClubButton() {
        leaveClubButton.click();
        confirm();
        return this;
    }

    @Step("Проверяем, что есть ошибка выхода из клуба")
    public ClubPage checkLeaveClubError() {
        errorMessage.shouldBe(visible);
        errorMessage.shouldHave(text("Не удалось покинуть клуб"));
        return this;
    }

    @Step("Нажимаем на кнопку ввода комментария")
    public ClubPage clickAddReviewButton() {
        addReviewButton.click();
        return this;
    }

    @Step("Заполняем оценку")
    public ClubPage enterAssessment(Integer value) {
        assessmentField.setValue(value.toString());
        return this;
    }

    @Step("Заполняем кол-во прочитанных страниц")
    public ClubPage enterReadPages(Integer value) {
        readPagesField.setValue(value.toString());
        return this;
    }

    @Step("Заполняем отзыв")
    public ClubPage enterReview(String value) {
        reviewField.setValue(value);
        return this;
    }

    @Step("Нажимаем на кнопку публикации отзыва")
    public ClubPage clickSaveReviewButton() {
        saveReviewButton.click();
        return this;
    }

    @Step("Проверяем, что отзыв создан")
    public ClubPage checkExistReview(String value) {
        reviewCardReview.$(byText(value)).should(exist);
        return this;
    }

    @Step("Проверяем, что данные созданного отзыва соответствуют заданным")
    public ClubPage checkCreatedReviewData(
            Integer readPaged,
            String review
    ) {
        reviewCardReadPages.shouldHave(text(readPaged.toString()));
        reviewCardReview.shouldHave(text(review));
        return this;
    }

    @Step("Нажимаем на кнопку удаления отзыва")
    public ClubPage clickDeleteReviewButton() {
        deleteReviewButton.click();
        confirm();
        return this;
    }

    @Step("Проверяем, что отзыв удален")
    public ClubPage checkDeletedReview(String value) {
        reviewCardReview.$(byText(value)).shouldNot(exist);
        return this;
    }

}
