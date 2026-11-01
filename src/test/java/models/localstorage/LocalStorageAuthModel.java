package models.localstorage;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import models.registration.SuccessfulRegistrationResponseModel;

public record LocalStorageAuthModel (
        SuccessfulRegistrationResponseModel user,
        String accessToken,
        String refreshToken,
        boolean isAuthenticated
) {
    private static final JsonMapper JSON = JsonMapper.builder().build();

    public String toJson() {
        try {
            return JSON.writeValueAsString(this);
        } catch (JacksonException e) {
            throw new RuntimeException(e);
        }
    }
}

