package angeelya.inPic.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.Resource;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageResourceResponse {
    private Long image_id;
    private Resource image;
}
