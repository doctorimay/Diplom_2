package User;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;

public class CreateUserTest {
    private UserClient userClient;
    private User user;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = User.getRandom();
    }

    @After
    public void tearDown() {
        JsonElement accessTokenFull = userClient.login(UserCredentials.from(user).toString()).thenReturn()
                .body().as(JsonObject.class).get("accessToken");
        if (accessTokenFull != null) {
            String accessToken = accessTokenFull.toString();
            TokenInfo.setAccessToken(accessToken);
            userClient.delete().then().statusCode(202);
        }

    }

    @Test // создать уникального пользователя;
    public void isUserCanCreatedTest() {
        userClient.create(user).then().assertThat()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test // создать пользователя, который уже зарегистрирован;
    public void itIsNotPossibleToCreateIdenticalUsersTest() {
        userClient.create(user).then().statusCode(200);
        userClient.create(user).then().assertThat()
                .statusCode(403)
                .body("message", equalTo("User already exists"));
    }

    @Test //создать пользователя и не заполнить одно из обязательных полей.
    public void creatingUserWithoutFillingInRequiredFieldTest() {
        User user2 = new User("", user.password, user.name);
        userClient.create(user2).then().assertThat()
                .statusCode(403)
                .body("message", equalTo("Email, password and name are required fields"));
    }
}