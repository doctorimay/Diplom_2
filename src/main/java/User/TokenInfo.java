package User;


public class TokenInfo {
    private static String accessToken;
    private static String refreshToken;

    public static String getAccessToken() {
        return accessToken;
    }

    public static void setAccessToken(String accessToken) {
        TokenInfo.accessToken = accessToken;
    }

    public static String getRefreshToken() {
        return refreshToken;
    }

    public static void setRefreshToken(String refreshToken) {
        TokenInfo.refreshToken = refreshToken;
    }
}