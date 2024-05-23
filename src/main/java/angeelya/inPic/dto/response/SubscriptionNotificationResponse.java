package angeelya.inPic.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionNotificationResponse {
    private Long friend_id;
    private String friendName;
    private String friendImage;
    private Boolean isRead;
}
