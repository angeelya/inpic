package angeelya.inPic.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriberRequest {
    @Min(value = 1,message = "User_id should be more than 1")
    @NotNull(message ="User_id should be not null" )
    private Long user_id;
    @Min(value = 1,message = "Subscriber_id should be more than 1")
    @NotNull(message ="Subscriber_id should be not null" )
    private Long subscriber_id;
}
