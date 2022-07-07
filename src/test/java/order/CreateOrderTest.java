package order;


import client.OrderClient;

import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import model.Order;
import model.UserCredentials;
import model.User;
import client.UserClient;

public class CreateOrderTest {
    private UserClient userClient;
    private OrderClient orderClient;
    private User user;
    String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = User.getRandomUser();
        userClient.create(user);
        orderClient = new OrderClient();
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.delete(accessToken);
        }
    }


    @Test // Создание заказа авторизаванным пользователем с ингридиентами
    public void authorizedСreateOrderWithIngredientsTest() {
        UserCredentials userCredentials = UserCredentials.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
        ValidatableResponse loginResponse = userClient.login(userCredentials);
        accessToken = loginResponse.extract().path("accessToken");

        ValidatableResponse orderResponse = orderClient.createOrderAuthorized(new Order(List.of("61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6f", "61c0c5a71d1f82001bdaaa72")), accessToken);
        int statusCode = orderResponse.extract().statusCode();
        boolean success = orderResponse.extract().path("success");
        String burgerName = orderResponse.extract().path("name");
        int orderNumber = orderResponse.extract().path("order.number");

        assertThat("User has created", statusCode, equalTo(SC_OK));
        assertThat("State if courier has created", success, equalTo(true));
        assertThat("State if courier has created", burgerName, notNullValue());
        assertThat("State if courier has created", orderNumber, notNullValue());
    }


    @Test // Создание заказа без ингридиентов
    public void authorizedСreateOrderWithoutIngredientsTest() {
        UserCredentials credentials = UserCredentials.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
        ValidatableResponse loginResponse = userClient.login(credentials);
        accessToken = loginResponse.extract().path("accessToken");
        ValidatableResponse orderResponse = orderClient.createOrderAuthorized(new Order(List.of()), accessToken);
        int statusCode = orderResponse.extract().statusCode();
        boolean success = orderResponse.extract().path("success");
        String message = orderResponse.extract().path("message");
        assertThat("User has created", statusCode, equalTo(SC_BAD_REQUEST));
        assertThat("State if courier has created", success, equalTo(false));
        assertThat("State if courier has created", message, equalTo("Ingredient ids must be provided"));
    }

    @Test // Создание заказа неавторизованным пользователем
    public void unauthorizedCreateOrderTest() {
        ValidatableResponse orderResponse = orderClient.createOrderUnauthorized(new Order(List.of("61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6f", "61c0c5a71d1f82001bdaaa72")));
        int statusCode = orderResponse.extract().statusCode();
        boolean success = orderResponse.extract().path("success");
        String burgerName = orderResponse.extract().path("name");
        int orderNumber = orderResponse.extract().path("order.number");

        assertThat("User has created", statusCode, equalTo(SC_OK));
        assertThat("State if courier has created", success, equalTo(true));
        assertThat("State if courier has created", burgerName, notNullValue());
        assertThat("State if courier has created", orderNumber, notNullValue());
    }

    @Test // Создание заказа с неверным хешем ингредиентов
    public void invalidIngredientsOrderCreateTest() {
        UserCredentials credentials = UserCredentials.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
        ValidatableResponse loginResponse = userClient.login(credentials);
        accessToken = loginResponse.extract().path("accessToken");
        ValidatableResponse orderResponse = orderClient.createOrderAuthorized(new Order(List.of("61c0c5a71d1f82001bdaaa6", "61c0c5a71d1f8201bdaaa6f", "61c0c5a71d1f8001bdaaa72")), accessToken);
        int statusCode = orderResponse.extract().statusCode();
        assertThat("User has created", statusCode, equalTo(SC_INTERNAL_SERVER_ERROR));
    }
}
