package User;


import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class UserClient extends RestAssureUser {
    public static final String PATH = URL + "auth/";



    public static Response create(String user) {
        return given()
                .spec(getBaseSpec())
                .body(user)
                .post(PATH + "register/");
    }


    public static Response login(String userLogin) {
        return given()
                .spec(getBaseSpec())
                .body(userLogin)
                .post(PATH + "login/");
    }


    public static Response change(String changeData) {
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(TokenInfo.getAccessToken())
                .body(changeData)
                .patch(PATH + "user/");
    }


    public static Response changeUnauthorized(String changeData) {
        return given()
                .spec(getBaseSpec())
                .body(changeData)
                .patch(PATH + "user/");
    }



    public static Response delete() {
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(TokenInfo.getAccessToken())
                .when()
                .delete(PATH + "user/");
    }

}