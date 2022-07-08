package order;


import client.OrderClient;

import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import model.Order;
import model.UserCredentials;
import model.User;
import client.UserClient;


import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class CreateOrderTest {
    UserClient userClient;
    OrderClient orderClient;
    User user;
    String accessToken;
    Order order;

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


    @Test // Создание заказа авторизаванным пользователем с ингридиентами
    public void authorizedCreateOrderWithIngredientsTest() {
        UserCredentials userCredentials = UserCredentials.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
        ValidatableResponse loginResponse = UserClient.login(userCredentials);
        accessToken = loginResponse.extract().path("accessToken");
        order = Order.getRealIngredients();
        ValidatableResponse response = OrderClient.createOrderAuthorized(accessToken, order);
        int statusCode = response.extract().statusCode();
        boolean success = response.extract().path("success");
        String number = response.extract().path("number");
        assertEquals("Wrong code", 200, statusCode);
        assertTrue("Must be true", success);
    }

    @Test // Создание заказа без ингридиентов
    public void authorizedCreateOrderWithoutIngredientsTest() {
        UserCredentials credentials = UserCredentials.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
        ValidatableResponse loginResponse = UserClient.login(credentials);
        accessToken = loginResponse.extract().path("accessToken");
        order = new Order();
        ValidatableResponse response = OrderClient.createOrderAuthorized(accessToken, order);
        int statusCode = response.extract().statusCode();
        boolean success = response.extract().path("success");
        String message = response.extract().path("message");
        assertEquals("Wrong code", 400, statusCode);
        assertFalse("Must be false", success);
        assertEquals("Wrong error message", "Ingredient ids must be provided", message);
    }


    @Test // Создание заказа неавторизованным пользователем
    public void unauthorizedCreateOrderTest() {
        ValidatableResponse orderResponse = OrderClient.createOrderUnauthorized(Order.getRealIngredients());
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
        ValidatableResponse loginResponse = UserClient.login(credentials);
        accessToken = loginResponse.extract().path("accessToken");
        order = Order.getAllUnrealIngredients();
        ValidatableResponse response = OrderClient.createOrderAuthorized(accessToken, order);
        int statusCode = response.extract().statusCode();
        assertEquals("Wrong code", 500, statusCode);
    }
}
