package angeelya.inPic.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.Resource;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LikeNotificationResponse {
    private Long actor_id;
    private String actorName;
    private Resource actorImage;
    private Long image_id;
    private Resource image;
    private Boolean isRead;
}
