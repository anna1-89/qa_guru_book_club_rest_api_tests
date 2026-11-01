package tests;

import api.ApiClient;
import com.codeborne.selenide.Configuration;
import helpers.Attach;
import io.restassured.RestAssured;
import net.datafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import pages.ClubPage;
import pages.CreateClubPage;
import pages.MainPage;

import static com.codeborne.selenide.Selenide.closeWebDriver;

public class TestBase {

    protected static final ApiClient api = new ApiClient();
    protected static final Faker faker = new Faker();

    public MainPage mainPage = new MainPage();
    public CreateClubPage createClubPage = new CreateClubPage();
    public ClubPage clubPage = new ClubPage();

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "https://book-club.qa.guru";
        RestAssured.basePath = "/api/v1";

        Configuration.baseUrl = "https://book-club.qa.guru";
        Configuration.browserSize = "1920x1080";
    }

    @AfterEach
    void afterEach() {
        Attach.screenshotAs("Last screenshot");
        Attach.pageSource();
        Attach.browserConsoleLogs();
        Attach.addVideo();
        closeWebDriver();
    }
}
