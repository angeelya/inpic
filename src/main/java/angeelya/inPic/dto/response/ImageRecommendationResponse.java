package angeelya.inPic.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.Resource;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageRecommendationResponse {
    private Long image_id;
    private Resource image;
    private String name;
}
