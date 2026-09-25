package models.updateUser;

public record SuccessfulUserUpdateResponseModel(
            Integer id,
            String username,
            String firstName,
            String lastName,
            String email,
            String remoteAddr) {
}
