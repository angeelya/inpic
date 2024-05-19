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
public class AdminNotificationResponse {
     private boolean isRead;
     private String cause;
     private Resource deletedImage;
}
