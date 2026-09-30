package models.clubs;

import java.util.List;

public record CreateClubWithBlankFieldsResponseModel(
        List<String> bookTitle,
        List<String> publicationYear,
        List<String> description,
        List<String> telegramChatLink
) {
}
