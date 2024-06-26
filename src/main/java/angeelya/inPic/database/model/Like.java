package angeelya.inPic.database.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "image_like")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinColumn(name = "user_id")
    @ManyToOne(optional = false)
    private User user;
    @JoinColumn(name = "image_id")
    @ManyToOne(optional = false)
    private Image image;
    @JsonIgnore
    @OneToOne(optional = false,mappedBy = "like",cascade = CascadeType.ALL)
    private LikeNotification likeNotification;
}
