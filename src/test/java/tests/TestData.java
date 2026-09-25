package tests;

public class TestData {

    public static final String LOGIN_USERNAME = "qaguru";
    public static final String LOGIN_PASSWORD = "qaguru123";
    public static final String LOGIN_WRONG_USERNAME = "qaguru123456";
    public static final String LOGIN_WRONG_PASSWORD = "123456";

    public static final String LOGIN_TOKEN_PREFIX = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
    public static final String LOGIN_WRONG_CREDENTIALS_ERROR = "Invalid username or password.";
    public static final String LOGIN_EMPTY_FIELD_ERROR = "This field may not be blank.";

    public static final String REGISTRATION_EXISTING_USER_ERROR = "A user with that username already exists.";
    public static final String REGISTRATION_EMPTY_FIELD_ERROR = "This field may not be blank.";

    public static final String LOGOUT_WRONG_TOKEN = "Token is invalid123456789";
    public static final String LOGOUT_WRONG_TOKEN_ERROR = "Token is invalid";
    public static final String LOGOUT_BLACKLISTED_TOKEN_ERROR = "Token is blacklisted";
    public static final String LOGOUT_WRONG_TOKEN_CODE_ERROR = "token_not_valid";
    public static final String LOGOUT_EMPTY_TOKEN_ERROR = "This field may not be blank.";

    public static String UPDATE_USER_WRONG_CREDENTIALS_ERROR = "Authentication credentials were not provided.";
    public static String UPDATE_USER_EXISTING_USER_ERROR = "A user with that username already exists.";
    public static String UPDATE_USER_BLANK_FIELD_ERROR = "This field may not be blank.";


    public static final String REGISTRATION_IP_REGEXP =
            "^((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}"
                    + "(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$";
}
