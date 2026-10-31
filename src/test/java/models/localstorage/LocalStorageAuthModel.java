package models.localstorage;

public record LocalStorageAuthModel (BookClubAuth book_club_auth) {
    public record BookClubAuth(
            User user,
            String accessToken,
            String refreshToken,
            boolean isAuthenticated
    ) {}

    public record User(
            Integer id,
            String username,
            String firstName,
            String lastName,
            String email,
            String remoteAddr
    ) {}
}
