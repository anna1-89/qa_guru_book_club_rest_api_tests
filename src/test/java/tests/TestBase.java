package tests;

import api.ApiClient;
import com.codeborne.selenide.Configuration;
import io.restassured.RestAssured;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeAll;
import pages.ClubPage;
import pages.CreateClubPage;
import pages.MainPage;

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
}
