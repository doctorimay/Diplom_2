package Order;

import User.User;
import User.UserClient;
import User.TokenInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

public class GettingOrdersFromUserTest {
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

    @Test // получение заказов авторизованного пользователя
    public void getAuthorizedUserOrderTest() {
        orderClient.getUserOrderAuthorized().then().assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("orders", notNullValue());
    }

    @Test // получение заказов неавторизованного пользователя
    public void getUnauthorizedUserOrderTest() {
        orderClient.getUserOrderUnauthorized().then().assertThat()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}
