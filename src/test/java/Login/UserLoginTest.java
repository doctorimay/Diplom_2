package Login;

import User.User;
import User.UserClient;
import User.UserCredentials;
import User.TokenInfo;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class UserLoginTest {
    private UserClient userClient;
    private User user;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = User.getRandom();
        userClient.create(user.toString());
    }
    @After
    public void tearDown() {
        JsonElement accessTokenFull = userClient.login(UserCredentials.from(user).toString()).thenReturn()
                .body().as(JsonObject.class).get("accessToken");
        if (accessTokenFull != null) {
            String accessToken = accessTokenFull.toString().substring(8, 179);
            TokenInfo.setAccessToken(accessToken);
            userClient.delete().then().statusCode(202);
        }
    }

    @Test //логин под существующим пользователем,
    public void successfulUserAuthorizationTest() {
        userClient.login(UserCredentials.from(user).toString()).then().assertThat()
                .statusCode(200)
                .body("accessToken", notNullValue());
    }

    @Test //логин с неверным логином и паролем.
    public void userAuthorizationWithIncorrectRequiredFieldTest() {
        UserCredentials userCredentials = new UserCredentials(user.login + "гыггыгыгы", user.password + "олололло");
        userClient.login(userCredentials.toString()).then().assertThat()
                .statusCode(401)
                .body("message", equalTo("email or password are incorrect"));
    }
}
