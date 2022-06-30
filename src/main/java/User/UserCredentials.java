package User;

public class UserCredentials {
    public String login;
    public String password;

    public UserCredentials(String login, String password){
        this.login = login;
        this.password = password;
    }

    public UserCredentials(User user) {
        this.login = user.getLogin();
        this.password = user.getPassword();
    }

    public static UserCredentials from (User user){
        return new UserCredentials(user);
    }


    @Override
    public String toString() {
        return "{\"" +
                "email\":\"" + login + "\",\"" +
                "password\":\"" + password + "\"}";
    }
}
