package angeelya.inPic.dto.response;

import angeelya.inPic.database.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JwtTokenResponse {
    private Long user_id;
    private String jwtToken;
    private String role;
}
