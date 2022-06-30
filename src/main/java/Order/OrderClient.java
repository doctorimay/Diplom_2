package Order;

import User.TokenInfo;
import User.RestAssureUser;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class OrderClient extends RestAssureUser {
    public static final String ORDER_PATH = URL + "orders/";
    String json;
    IngredientList ingredientList = new IngredientList();


    public String ingredientCreate() {
        ingredientList.ingredientRequestCreate();
        json = "{" + ingredientList.orderRequest + "}";
        return json;
    }

    public Response createOrderAuthorized() {
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(TokenInfo.getAccessToken())
                .body(json)
                .post(ORDER_PATH);
    }

    public Response getUserOrderAuthorized() {
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(TokenInfo.getAccessToken())
                .get(ORDER_PATH);
    }

    public Response createOrderUnauthorized() {
        return given()
                .spec(getBaseSpec())
                .body(json)
                .post(ORDER_PATH);

    }

    public Response getUserOrderUnauthorized() {
        return given()
                .spec(getBaseSpec())
                .get(ORDER_PATH);
    }

}
