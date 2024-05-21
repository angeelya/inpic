package angeelya.inPic.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImagePageResponse {
    private Long user_id;
    private String imgSystemName;
    private String Name;
    private String userName;
    private String imgDescription;
    private String userImg;
    private String image;
    private Integer likeCount;

}
