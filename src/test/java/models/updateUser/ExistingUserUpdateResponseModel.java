package models.updateUser;

import java.util.List;

public record ExistingUserUpdateResponseModel(
        List<String> username) {
}
