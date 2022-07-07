package model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    public String email;
    public String password;
    public String name;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public static User getRandomUser() {

        String email = RandomStringUtils.randomAlphabetic(10) + "@google.ru";
        String password = RandomStringUtils.randomAlphabetic(10);
        String name = RandomStringUtils.randomAlphabetic(10);
        return new User(email, password, name);
    }
}
