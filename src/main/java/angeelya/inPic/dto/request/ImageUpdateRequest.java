package angeelya.inPic.dto.request;

import angeelya.inPic.validation.annotation.NameContent;
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
public class ImageUpdateRequest {
    @Min(value = 1,message = "User_id should be more than 1")
    @NotNull(message ="User_id should be not null")
    private Long user_id;
    @Min(value = 1,message = "Image_id should be more than 1")
    @NotNull(message ="Image_id should be not null")
    private Long image_id;
    @NotNull(message ="Name should be not null" )
    @Size(min = 1, max=250, message = "Name should be more than 1 and less than 250")
    @NameContent
    private String name;
    @NotNull(message ="Description should be not null" )
    @Size(min = 1, max=250, message = "Description should be more than 1 and less than 250")
    private String description;
    @Min(value = 1,message = "Category_id should be more than 1")
    @NotNull(message ="Category_id should be not null")
    private Long category_id;
}
