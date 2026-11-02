package tests;

import api.ApiClient;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import helpers.Attach;
import io.qameta.allure.selenide.AllureSelenide;
import io.restassured.RestAssured;
import net.datafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import pages.ClubPage;
import pages.CreateClubPage;
import pages.MainPage;

import java.util.List;
import java.util.Map;

import static com.codeborne.selenide.Selenide.closeWebDriver;

public class TestBase {

    protected static final ApiClient api = new ApiClient();
    protected static final Faker faker = new Faker();

    public MainPage mainPage = new MainPage();
    public CreateClubPage createClubPage = new CreateClubPage();
    public ClubPage clubPage = new ClubPage();

    @BeforeAll
    public static void setUp() {
        //RestAssured.baseURI = "https://book-club.qa.guru";
        RestAssured.basePath = "/api/v1";

        //Configuration.baseUrl = "https://book-club.qa.guru";
        //Configuration.browserSize = "1920x1080";

        RestAssured.baseURI = System.getProperty("base_URI", "https://book-club.qa.guru/");;

        Configuration.baseUrl = System.getProperty("url", "https://book-club.qa.guru");
        Configuration.browser = System.getProperty("browser", "chrome");
        Configuration.browserSize = System.getProperty("browserSize", "1920x1080");
        Configuration.browserVersion = System.getProperty("browserVersion", "148.0");
        Configuration.headless = Boolean.parseBoolean(System.getProperty("headless", "false"));

        String selenoidUrl = System.getProperty("selenoidUrl");
        if (selenoidUrl != null && !selenoidUrl.isBlank()) {
            Configuration.remote = selenoidUrl;
        }

        DesiredCapabilities capabilities = new DesiredCapabilities();
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments(List.of("--disable-dev-shm-usage", "--no-sandbox"));
        capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
        capabilities.setCapability("selenoid:options", Map.<String, Object>of(
                "enableVNC", true,
                "enableVideo", true
        ));
        Configuration.browserCapabilities = capabilities;
    }

    @BeforeEach
    void addListener() {
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide());
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
