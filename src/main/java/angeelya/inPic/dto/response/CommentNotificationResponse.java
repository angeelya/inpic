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
public class CommentNotificationResponse {
    private Long actor_id;
    private String actorName;
    private String actorImage;
    private Long image_id;
    private String text;
    private Boolean isRead;
}
