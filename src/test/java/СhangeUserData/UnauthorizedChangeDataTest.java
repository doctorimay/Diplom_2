package СhangeUserData;

import User.User;
import User.UserClient;
import User.UserCredentials;
import User.TokenInfo;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;

public class UnauthorizedChangeDataTest {

    private UserClient userClient;
    private User user;
    private String accessToken;

    @Before //создаем случайного пользователя
    public void setUp() {
        userClient = new UserClient();
        user = User.getRandom();
        userClient.create(user);
        JsonElement accessTokenFull = userClient.login(UserCredentials.from(user).toString()).thenReturn()
                .body().as(JsonObject.class).get("accessToken");
        accessToken = accessTokenFull.toString();
        TokenInfo.setAccessToken(accessToken);
    }

    @After //удаляем созданного пользователя
    public void tearDown() {
        if (accessToken != null) {
            userClient.delete().then().statusCode(202);
        }
    }

    @Test // изменение имени пользователя без авторизации
    public void changeUserNameTest() {
        String changeData = "{\"" + "name" + "\":" + "\"" + RandomStringUtils.randomAlphabetic(5) + user.name + "\"}";
        userClient.changeUnauthorized(changeData).then().assertThat()
                .statusCode(401)
                .body("message", equalTo("You should be authorised"));
    }

    @Test // изменение мейла пользователя без авторизации
    public void changeUserEmailTest() {
        String changeData = "{\"" + "email" + "\":" + "\"" + RandomStringUtils.randomAlphabetic(5) + user.login + "\"}";
        userClient.changeUnauthorized(changeData).then().assertThat()
                .statusCode(401)
                .body("message", equalTo("You should be authorised"));
    }

    @Test //// изменение пароля пользователя без авторизации
    public void changeUserPasswordTest() {
        String changeData = "{\"" + "password" + "\":" + "\"" + RandomStringUtils.randomAlphabetic(5) + user.password + "\"}";
        userClient.changeUnauthorized(changeData).then().assertThat()
                .statusCode(401)
                .body("message", equalTo("You should be authorised"));
    }

}
