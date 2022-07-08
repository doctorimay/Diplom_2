package changeUserData;


import io.restassured.response.ValidatableResponse;
import model.User;
import client.UserClient;
import model.UserCredentials;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;




public class UserDataChangeTest {

    UserClient userClient;
    private User user;
    String accessToken;
    ValidatableResponse loginResponse;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = User.getRandomUser();
        UserClient.create(user);

    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            UserClient.delete(accessToken);
        }
    }


    @Test
    public void changeUserDataPositiveTest() {
        UserCredentials credentials = UserCredentials.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
        loginResponse = UserClient.login(credentials);
        accessToken = loginResponse.extract().path("accessToken");
        User newUser = User.getRandomUser();
        ValidatableResponse updateResponse = UserClient.change(accessToken, newUser);

        int statusCode = updateResponse.extract().statusCode();
        boolean success = updateResponse.extract().path("success");
        String email = updateResponse.extract().path("user.email");
        String name = updateResponse.extract().path("user.name");

        assertThat("Status code is not correct", statusCode, equalTo(SC_OK));
        assertThat("The data must be changed", success, equalTo(true));
        assertThat("Email is not correct", email, equalTo(newUser.getEmail()));
        assertThat("Name is not correct", name, equalTo(newUser.getName()));
    }

    @Test
    public void changeUserDataNegativeTest() {
        accessToken = "";
        User newUser = User.getRandomUser();
        ValidatableResponse updateResponse = UserClient.change(accessToken, newUser);

        int statusCode = updateResponse.extract().statusCode();
        boolean success = updateResponse.extract().path("success");
        String message = updateResponse.extract().path("message");

        assertThat("Status code is not correct", statusCode, equalTo(SC_UNAUTHORIZED));
        assertThat("The data must not be changed", success, equalTo(false));
        assertThat("The error massage is not correct", message, equalTo("You should be authorised"));

    }
}
