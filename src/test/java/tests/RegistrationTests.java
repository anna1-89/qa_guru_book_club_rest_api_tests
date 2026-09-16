package tests;

import models.pojo.RegistrationBodyPojoModel;
import models.pojo.RegistrationResponsePojoModel;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RegistrationTests {

    String username;
    String password;

    @BeforeEach
    public void prepareTestData() {
        Faker faker = new Faker();
        username = faker.name().firstName();
        password = faker.name().firstName();
    }

    @Test
    public void successfulRegistrationTest_bad_practice() {
        //https://book-club.qa.guru/api/v1/users/register/

        String data = "";

        given()
                .log().all()
                .contentType(JSON)
//                .header("content-type", ContentType.JSON)
                .body(data)
                .when()
                .post("https://book-club.qa.guru/api/v1/users/register/")
                .then()
                .log().all()
                .statusCode(201)
                .body("username", is(username))
                .body("id", notNullValue());

    }

    @Test
    public void successfulRegistrationTest_with_pojo() {
        //https://book-club.qa.guru/api/v1/users/register/


        RegistrationBodyPojoModel data = new RegistrationBodyPojoModel();
        data.setUsername(username);
        data.setPassword(password);


//        With constructor
//        RegistrationBodyPojoModel data = new RegistrationBodyPojoModel(username, password);

        RegistrationResponsePojoModel registrationResponse = given()
                .log().all()
                .contentType(JSON)
//                .header("content-type", ContentType.JSON)
                .body(data)
                .when()
                .post("https://book-club.qa.guru/api/v1/users/register/")
                .then()
                .log().all()
                .statusCode(201)
                .extract()
                .as(RegistrationResponsePojoModel.class);

                assertEquals(username, registrationResponse.getUsername());


    }

    @Test
    public void existingUser400Test() {
        //https://book-club.qa.guru/api/v1/users/register/


        String data = "";

        given()
                .log().all()
                .contentType(JSON)
//                .header("content-type", ContentType.JSON)
                .body(data)
                .when()
                .post("https://book-club.qa.guru/api/v1/users/register/")
                .then()
                .log().all()
                .statusCode(201)
                .body("username", is(username))
                .body("id", notNullValue());

        given()
                .log().all()
                .contentType(JSON)
//                .header("content-type", ContentType.JSON)
                .body(data)
                .when()
                .post("https://book-club.qa.guru/api/v1/users/register/")
                .then()
                .log().all()
                .statusCode(400)
                .body("username[0]", is("A user with that username is already exists."));

    }

    @Test
    public void invalidUsername400Test() {
        //https://book-club.qa.guru/api/v1/users/register/


        String data = "";

        given()
                .log().all()
                .contentType(JSON)
//                .header("content-type", ContentType.JSON)
                .body(data)
                .when()
                .post("https://book-club.qa.guru/api/v1/users/register/")
                .then()
                .log().all()
                .statusCode(201)
                .body("username", is(username))
                .body("id", notNullValue());

    }

    @Test
    public void negativeRegistration500Test() {
        //https://book-club.qa.guru/api/v1/users/register/


        String data = "";

        given()
                .body(data)
                .when()
                .post("https://book-club.qa.guru/api/v1/users/register/")
                .then()
                .statusCode(201)
                .body("username", is(username))
                .body("id", notNullValue());

    }

    @Test
    public void nunsupportedMediaType415Test() {
        //https://book-club.qa.guru/api/v1/users/register/


        String data = "";

        given()
                .body(data)
                .when()
                .post("https://book-club.qa.guru/api/v1/users/register/")
                .then()
                .statusCode(201)
                .body("username", is(username))
                .body("id", notNullValue());

    }
}
