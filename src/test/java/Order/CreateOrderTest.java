package Order;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import User.TokenInfo;
import User.User;
import User.UserClient;

import static org.hamcrest.Matchers.*;

public class CreateOrderTest {
    private UserClient userClient;
    private User user;
    private OrderClient orderClient;
    String accessToken;

    @Before
    public void setUp() {
        orderClient = new OrderClient();
        userClient = new UserClient();
        user = User.getRandom();
        String accessTokenFull = userClient.create(user).then().extract().body().path("accessToken");
        accessToken = accessTokenFull;
        if (accessToken != null) {
            TokenInfo.setAccessToken(accessToken);
        }
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.delete().then().statusCode(202);
        }
    }

    @Test // Создание заказа авторизаванным пользователем с ингридиентами
    public void authorizedСreateOrderWithIngredientsTest() {
        orderClient.ingredientCreate();
        orderClient.createOrderAuthorized().then().assertThat()
                .statusCode(200)
                .body("order.createdAt", notNullValue())
                .body("order.owner", notNullValue());
    }

    @Test // Создание заказа без ингридиентов
    public void authorizedСreateOrderWithoutIngredientsTest() {
        orderClient.json = "{" + "\"ingredients\":[]" + "}";
        orderClient.createOrderAuthorized().then().assertThat()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test // Создание заказа неавторизованным пользователем
    public void unauthorizedCreateOrderTest() {
        orderClient.ingredientCreate();
        orderClient.createOrderUnauthorized().then().assertThat()
                .statusCode(200)
                .body("order.createdAt", nullValue())
                .body("order.owner", nullValue());
    }

    @Test // Создание заказа с неверным хешем ингредиентов
    public void invalidIngredientsOrderCreateTest() {
        IngredientList ingredientList = new IngredientList();
        ingredientList = ingredientList.getAllIngredients();
        String ingredientOrder = ingredientList.getData().get(1).get_id();
        orderClient.json = "{" + "\"ingredients\":[\"" + ingredientOrder + "masseffect\"]}";
        orderClient.createOrderAuthorized().then().assertThat()
                .statusCode(500);
    }
}
