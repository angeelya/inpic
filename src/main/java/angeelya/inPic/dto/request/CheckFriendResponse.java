package angeelya.inPic.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckFriendResponse {
    private Boolean haveFriend;
}
