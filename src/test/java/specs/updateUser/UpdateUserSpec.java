package specs.updateUser;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.filter.log.LogDetail.ALL;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.notNullValue;

public class UpdateUserSpec {

    public static ResponseSpecification successfulUpdateUserResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(200)
            .expectBody("id", notNullValue())
            .expectBody("username", notNullValue())
            .expectBody("firstName", notNullValue())
            .expectBody("lastName", notNullValue())
            .expectBody("email", notNullValue())
            .expectBody("remoteAddr", notNullValue())
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/updateUser/successful_update_user_response_schema.json"))
            .build();

    public static ResponseSpecification unauthorizedUpdateUserResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(401)
            .expectBody((matchesJsonSchemaInClasspath(
                    "schemas/updateUser/unauthorized_update_user_response_schema.json")))
            .build();

    public static ResponseSpecification existingUserUpdateUserResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(400)
            .expectBody((matchesJsonSchemaInClasspath(
                    "schemas/updateUser/existing_user_update_response_schema.json")))
            .build();

    public static ResponseSpecification blankUsernameUpdateUserResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(400)
            .expectBody((matchesJsonSchemaInClasspath(
                    "schemas/updateUser/blank_username_user_update_response_schema.json")))
            .build();
}
