package angeelya.inPic.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlbumRequest {
    @Min(value = 1,message = "Album_id should be more than 1")
    @NotNull(message ="Album_id should be not null")
    private Long album_id;
}
