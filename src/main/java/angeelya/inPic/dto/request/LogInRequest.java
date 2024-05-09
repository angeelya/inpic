package angeelya.inPic.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LogInRequest {
    @NotNull(message ="Login should be not null" )
    private String login;
    @NotNull(message ="Password should be not null" )
    private String password;
}
