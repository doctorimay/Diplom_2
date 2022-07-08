package client;


import io.restassured.response.ValidatableResponse;
import model.Order;

import static io.restassured.RestAssured.given;

public class OrderClient extends RestAssureClient {

    public static final String ORDER_PATH = URL + "orders/";


    public static ValidatableResponse createOrderAuthorized(Order order, String accessToken) {
        return given()
                .spec(getBaseSpec())
                .headers("Authorization", accessToken)
                .body(order)
                .post(ORDER_PATH)
                .then();
    }

    public static ValidatableResponse getUserOrderAuthorized(String accessToken) {
        return given()
                .spec(getBaseSpec())
                .headers("Authorization", accessToken)
                .get(ORDER_PATH)
                .then();
    }

    public static ValidatableResponse createOrderUnauthorized(Order order) {
        return given()
                .spec(getBaseSpec())
                .body(order)
                .post(ORDER_PATH)
                .then();

    }

    public static ValidatableResponse  getUserOrderUnauthorized() {
        return given()
                .spec(getBaseSpec())
                .get(ORDER_PATH)
                .then();
    }

}
