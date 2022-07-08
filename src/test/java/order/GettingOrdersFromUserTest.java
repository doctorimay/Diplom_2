package order;

import client.OrderClient;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import model.User;
import client.UserClient;
import model.UserCredentials;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;


public class GettingOrdersFromUserTest {
    private UserClient userClient;
    private OrderClient orderClient;
    private User user;
    String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = User.getRandomUser();
        UserClient.create(user);
        orderClient = new OrderClient();

    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            UserClient.delete(accessToken);
        }
    }

    @Test // получение заказов авторизованного пользователя
    public void getAuthorizedUserOrderTest() {
        UserCredentials userCredentials = UserCredentials.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
        ValidatableResponse loginResponse = UserClient.login(userCredentials);
        accessToken = loginResponse.extract().path("accessToken");
        Response orderResponse = OrderClient.getUserOrderAuthorized(accessToken);
        int statusCode = orderResponse.statusCode();
        boolean success = orderResponse.path("success");
        String total = orderResponse.path("total");
        assertThat("User has created", statusCode, equalTo(SC_OK));
        assertThat("State if courier has created", success, equalTo(true));
        assertThat("State if courier has created", total, notNullValue());
    }

    @Test // получение заказов неавторизованного пользователя
    public void getUnauthorizedUserOrderTest() {
        Response orderResponse = OrderClient.getUserOrderUnauthorized();
        int statusCode = orderResponse.statusCode();
        boolean success = orderResponse.path("success");
        String message = orderResponse.path("message");
        assertThat("User has created", statusCode, equalTo(SC_UNAUTHORIZED));
        assertThat("State if courier has created", success, equalTo(false));
        assertThat("State if courier has created", message, equalTo("You should be authorised"));
    }
}
