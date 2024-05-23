package angeelya.inPic.dto.request;

import angeelya.inPic.validation.annotation.EmailContent;
import angeelya.inPic.validation.annotation.LoginContent;
import angeelya.inPic.validation.annotation.UniqueEmail;
import angeelya.inPic.validation.annotation.UniqueLogin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotNull(message ="Login should be not null")
    @UniqueLogin
    @LoginContent
    @Size(min = 2, max=200 , message = "Login should be between 2 and 200")
    public String login;
    @NotNull(message ="Email should be not null" )
    @Size(min = 10, max=250, message = "Email should be more than 10 and less than 250")
    @UniqueEmail
    @EmailContent
    private String email;
    @NotNull(message ="Password should be not null" )
    @Size(min = 5, max = 350,message = "Password should contain more than 5 and less then 350 symbols")
    private String password;
}