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
public class CommentAddRequest {
    @Min(value = 1,message = "User_id should be more than 1")
    @NotNull(message ="User_id should be not null" )
    private Long user_id;
    @Min(value = 1,message = "Image_id should be more than 1")
    @NotNull(message ="Image_id should be not null" )
    private Long image_id;
    @NotNull(message ="Text should be not null" )
    @Size(min = 1, max=250, message = "Text should be more than 1 and less than 250")
    private String text;
}
