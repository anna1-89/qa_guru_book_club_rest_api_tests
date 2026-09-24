package models.login;

import java.util.List;

public record EmptyUserAndPasswordResponseModel(
        List<String> username,
        List<String> password
) {
}
