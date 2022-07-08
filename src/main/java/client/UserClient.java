package client;

import io.qameta.allure.internal.shadowed.jackson.core.JsonProcessingException;
import io.qameta.allure.internal.shadowed.jackson.databind.ObjectMapper;
import io.restassured.response.ValidatableResponse;
import model.User;
import model.UserCredentials;

import static io.restassured.RestAssured.given;

public class UserClient extends RestAssureClient {

    public static final String PATH = URL + "auth/";

    public static ValidatableResponse create(User user) {
        String registerRequestBody = null;

        try {
            registerRequestBody = new ObjectMapper().writeValueAsString(user);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return given()
                .spec(getBaseSpec())
                .body(registerRequestBody)
                .when()
                .post(PATH + "register")
                .then();
    }

    public static ValidatableResponse login(UserCredentials userCredentials) {

        return given()
                .spec(getBaseSpec())
                .body(userCredentials)
                .when()
                .post(PATH + "login")
                .then();
    }


    public static ValidatableResponse change(String accessToken, User user) {
        return given()
                .header("Authorization", accessToken)
                .spec(getBaseSpec())
                .body(user)
                .when()
                .patch(PATH + "user")
                .then();
    }

    public static ValidatableResponse delete(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .spec(getBaseSpec())
                .when()
                .delete(PATH + "user")
                .then();
    }
}