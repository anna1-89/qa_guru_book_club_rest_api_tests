package models.registration;

import java.util.List;

public record EmptyUsernameAndPasswordResponseModel(
        List<String> username,
        List<String> password
) {
}
