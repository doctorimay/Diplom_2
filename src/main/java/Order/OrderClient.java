package Order;

import User.TokenInfo;
import User.RestAssureUser;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class OrderClient extends RestAssureUser {
    public static final String ORDER_PATH = URL + "orders/";
    Object json;
    IngredientList ingredientList = new IngredientList();


    public String ingredientCreate() {
        ingredientList.ingredientRequestCreate();
        json = "{" + ingredientList.orderRequest + "}";
        return (String) json;
    }

    public Response createOrderAuthorized() {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", TokenInfo.getAccessToken())
                .body(json)
                .post(ORDER_PATH);
    }

    public Response getUserOrderAuthorized() {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", TokenInfo.getAccessToken())
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
