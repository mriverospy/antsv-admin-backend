package py.gov.mitic.htv.dto.token;

import jakarta.validation.constraints.NotBlank;

public class TokenRefreshRequest {

    @NotBlank
    private String accessToken;

    @NotBlank
    private String refreshToken;

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
