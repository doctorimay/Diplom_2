package User;

import lombok.Data;
import org.apache.commons.lang3.RandomStringUtils;

public @Data class User {

    public String login;
    public String password;
    public String name;

    public User(String login, String password, String name) {
        this.login = login;
        this.password = password;
        this.name = name;
    }

    public static User getRandom() {

        String userLogin = RandomStringUtils.randomAlphabetic(10) + "@google.ru";
        String userPassword = RandomStringUtils.randomAlphabetic(10);
        String userName = RandomStringUtils.randomAlphabetic(10);

        return new User(userLogin, userPassword, userName);
    }

    @Override
    public String toString() {
        return "{\"" +
                "email\":\"" + login + "\",\"" +
                "password\":\"" + password + "\",\"" +
                "name\":\"" + name + "\"}";
    }
}