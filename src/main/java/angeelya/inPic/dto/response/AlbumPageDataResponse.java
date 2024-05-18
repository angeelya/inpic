package angeelya.inPic.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlbumPageDataResponse {
    private Long album_id;
    private String name;
    private List<ImageResourceResponse> imageResourceResponses;
}
