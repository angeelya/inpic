package angeelya.inPic.dto.request;

import angeelya.inPic.validation.annotation.NameContent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlbumUpdateRequest {
    @Min(value = 1,message = "Album_id should be more than 1")
    @NotNull(message ="Album_id should be not null")
    private Long album_id;
    @NotNull(message ="Name should be not null")
    @NameContent
    @Size(min = 1, max=250, message = "Name should be more than 1 and less than 250")
    private String name;
    @NotNull(message ="Security should be not null")
    private Boolean security;
}
