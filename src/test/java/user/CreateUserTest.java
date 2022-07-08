package user;

import client.UserClient;
import io.restassured.response.ValidatableResponse;
import model.User;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.equalTo;

public class CreateUserTest {

    UserClient userClient;
    private User user;
    String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            UserClient.delete(accessToken);
        }
    }

    @Test // создать уникального пользователя;
    public void isUserCanCreatedTest() {
        user = User.getRandomUser();
        ValidatableResponse createResponse = UserClient.create(user);
        int statusCode = createResponse.extract().statusCode();
        boolean success = createResponse.extract().path("success");
        String accessToken = createResponse.extract().path("accessToken");
        String refreshToken = createResponse.extract().path("refreshToken");
        String email = createResponse.extract().path("user.email");
        String name = createResponse.extract().path("user.name");
        assertThat("Status code is not correct", statusCode, equalTo(SC_OK));
        assertThat("User not created", success, equalTo(true));
        assertThat("AccessToken is not correct", accessToken, startsWith("Bearer"));
        assertThat("RefreshToken is not correct", refreshToken, notNullValue());
        assertThat("Email is not correct ", email, equalTo(user.getEmail()));
        assertThat("Name is not correct", name, equalTo(user.getName()));
    }


    @Test // создать пользователя, который уже зарегистрирован;
    public void itIsNotPossibleToCreateIdenticalUsersTest() {
        user = User.getRandomUser();
        UserClient.create(user);
        ValidatableResponse createResponse = UserClient.create(user);
        int statusCode = createResponse.extract().statusCode();
        boolean success = createResponse.extract().path("success");
        String message = createResponse.extract().path("message");
        assertThat("Status code is not correct", statusCode, equalTo(SC_FORBIDDEN));
        assertThat("User must not be created", success, equalTo(false));
        assertThat("The error massage is not correct", message, equalTo("User already exists"));
    }

    @Test //создать пользователя и не заполнить одно из обязательных полей.
    public void creatingUserWithoutFillingInRequiredFieldTest() {
        user = User.builder()
                .email(RandomStringUtils.randomAlphabetic(7) + "@google.com")
                .name(RandomStringUtils.randomAlphabetic(10))
                .build();
        ValidatableResponse createResponse = UserClient.create(user);
        int statusCode = createResponse.extract().statusCode();
        boolean success = createResponse.extract().path("success");
        String message = createResponse.extract().path("message");
        assertThat("Status code is not correct", statusCode, equalTo(SC_FORBIDDEN));
        assertThat("User must not be created", success, equalTo(false));
        assertThat("The error massage is not correct", message, equalTo("Email, password and name are required fields"));
}
}