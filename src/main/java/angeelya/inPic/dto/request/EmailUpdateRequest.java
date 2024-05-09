package angeelya.inPic.dto.request;

import angeelya.inPic.validation.annotation.EmailContent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailUpdateRequest {
    @Min(value = 1,message = "User_id should be more than 1")
    @NotNull(message ="User_id should be not null" )
    private Long user_id;
    @NotNull(message ="Email should be not null" )
    @EmailContent
    private String email;
}
