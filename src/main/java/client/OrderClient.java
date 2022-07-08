package client;


import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import model.Order;

import static io.restassured.RestAssured.given;

public class OrderClient extends RestAssureClient {

    public static final String ORDER_PATH = URL + "orders/";

    public static ValidatableResponse createOrderAuthorized(String token, Order order) {
        return given()
                .spec(getBaseSpec())
                .headers("Authorization", token)
                .body(order)
                .post(ORDER_PATH)
                .then();
    }


    public static ValidatableResponse createOrderUnauthorized(Order order) {
        return given()
                .spec(getBaseSpec())
                .body(order)
                .when()
                .post(ORDER_PATH)
                .then();
    }


    public static ValidatableResponse getUserOrderAuthorized(String token) {
        return given()
                .spec(getBaseSpec())
                .headers("Authorization", token)
                .get(ORDER_PATH)
                .then();
    }


    public static Response getUserOrderUnauthorized() {
        return given()
                .spec(getBaseSpec())
                .when()
                .get(ORDER_PATH);
    }

}
