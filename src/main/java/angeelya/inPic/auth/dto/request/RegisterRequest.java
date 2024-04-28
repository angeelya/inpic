package angeelya.inPic.auth.dto.request;

import angeelya.inPic.auth.validation.UniqueEmail;
import angeelya.inPic.auth.validation.UniqueLogin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @UniqueLogin
    @NotNull
    public String login;
    @UniqueEmail
    @NotNull
    private String email;
    @NotNull
    private String password;
}