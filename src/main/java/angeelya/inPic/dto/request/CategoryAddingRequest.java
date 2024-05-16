package angeelya.inPic.dto.request;
import angeelya.inPic.validation.annotation.CategoryContent;
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
public class CategoryAddingRequest {
    @NotNull(message ="Category should be not null" )
    @Size(min = 1, max=250, message = "Category should be more than 1 and less than 250")
    @CategoryContent
    public String category;
}
