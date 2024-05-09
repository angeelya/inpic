package angeelya.inPic.dto.request;
import angeelya.inPic.validation.annotation.CategoryContent;
import jakarta.validation.constraints.NotNull;
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
    @CategoryContent
    public String category;
}
