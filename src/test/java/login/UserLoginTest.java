package login;


import io.restassured.response.ValidatableResponse;
import model.User;
import client.UserClient;
import model.UserCredentials;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.Locale;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class  UserLoginTest {

    UserClient userClient;
    public User user;
    final String email = RandomStringUtils.randomAlphabetic(10) + "@google.com";
    final String password = RandomStringUtils.randomAlphabetic(10);
    String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = new User(email, password, "TestName");
        UserClient.create(user);
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            UserClient.delete(accessToken);
        }
    }

    @Test //логин под существующим пользователем,
    public void successfulUserAuthorizationTest() {
        UserCredentials credentials = UserCredentials.builder()
                .email(user.getEmail().toLowerCase())
                .password(user.getPassword())
                .build();
        ValidatableResponse loginResponse = UserClient.login(credentials);

        int statusCode = loginResponse.extract().statusCode();
        boolean success = loginResponse.extract().path("success");
        accessToken = loginResponse.extract().path("accessToken");
        String refreshToken = loginResponse.extract().path("refreshToken");
        String email = loginResponse.extract().path("user.email");
        String name = loginResponse.extract().path("user.name");

        assertThat("Status code is not correct", statusCode, equalTo(SC_OK));
        assertThat("Authorization attempt failed", success, equalTo(true));
        assertThat("AccessToken is not correct", accessToken, startsWith("Bearer"));
        assertThat("RefreshToken is not correct", refreshToken, notNullValue());
        assertThat("Email is not correct ", email, equalTo(user.getEmail().toLowerCase(Locale.ROOT)));
        assertThat("Name is not correct", name, equalTo(user.getName()));
    }

    @Test //логин с неверным логином и паролем.
    public void userAuthorizationWithIncorrectRequiredFieldTest() {
        UserCredentials userCredentials = UserCredentials.builder()
                .email(user.getEmail())
                .password(user.getPassword() + "5")
                .build();
        ValidatableResponse loginResponse = UserClient.login(userCredentials);

        int statusCode = loginResponse.extract().statusCode();
        boolean success = loginResponse.extract().path("success");
        String message = loginResponse.extract().path("message");

        assertThat("Status code is not correct", statusCode, equalTo(SC_UNAUTHORIZED));
        assertThat("The login attempt must fail", success, equalTo(false));
        assertThat("The error massage is not correct", message, equalTo("email or password are incorrect"));
    }
}