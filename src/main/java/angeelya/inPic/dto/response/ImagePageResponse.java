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
    private String imgName;
    private String userName;
    private String imgDescription;
    private byte[] userImg;
    private byte[] image;
    private Integer likeCount;

}
