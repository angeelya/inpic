package angeelya.inPic.dto.request;

import angeelya.inPic.validation.annotation.NameContent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AlbumAddRequest {
    @Min(value = 1,message = "User_id should be more than 1")
    @NotNull(message ="User_id should be not null")
    private Long user_id;
    @NotNull(message ="Name should be not null")
    @NameContent
    @Size(min = 1, max=250, message = "Name should be more than 1 and less than 250")
    private String name;
    @NotNull(message ="Security should be not null")
    private Boolean security;
    private List<ImageRequest> imageRequests;
}
