package angeelya.inPic.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordUpdateRequest {
    @Min(value = 1,message = "User_id should be more than 1")
    @NotNull(message ="User_id should be not null" )
    private Long user_id;
    @Size(min = 1, max=250, message = "Old password should be more than 1 and less than 250")
    @NotNull(message ="Old password should be not null" )
    private String oldPassword;
    @Size(min = 1, max=250, message = "New password should be more than 1 and less than 250")
    @NotNull(message ="New password should be not null" )
    private String newPassword;
}
