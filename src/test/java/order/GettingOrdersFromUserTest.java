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


import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class GettingOrdersFromUserTest {
    UserClient userClient;
    OrderClient orderClient;
    User user;
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
        ValidatableResponse response = OrderClient.getUserOrderAuthorized(accessToken);
        int statusCode = response.extract().statusCode();
        boolean success = response.extract().path("success");
        assertEquals("Wrong code", 200, statusCode);
        assertTrue("Must be true", success);
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


