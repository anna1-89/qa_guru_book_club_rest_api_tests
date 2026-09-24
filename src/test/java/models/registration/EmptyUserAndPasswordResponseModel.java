package models.registration;

import java.util.List;

public record EmptyUserAndPasswordResponseModel(
        List<String> username,
        List<String> password
) {
}
