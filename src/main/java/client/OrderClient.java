package client;


import io.restassured.response.ValidatableResponse;
import model.Order;

import static io.restassured.RestAssured.given;

public class OrderClient extends RestAssureClient {

    public static final String ORDER_PATH = URL + "orders/";


    public ValidatableResponse createOrderAuthorized(Order order, String accessToken) {
        return given()
                .spec(getBaseSpec())
                .headers("Authorization", accessToken)
                .body(order)
                .post(ORDER_PATH)
                .then();
    }

    public ValidatableResponse getUserOrderAuthorized(String accessToken) {
        return given()
                .spec(getBaseSpec())
                .headers("Authorization", accessToken)
                .get(ORDER_PATH)
                .then();
    }

    public ValidatableResponse createOrderUnauthorized(Order order) {
        return given()
                .spec(getBaseSpec())
                .body(order)
                .post(ORDER_PATH)
                .then();

    }

    public ValidatableResponse  getUserOrderUnauthorized() {
        return given()
                .spec(getBaseSpec())
                .get(ORDER_PATH)
                .then();
    }

}
