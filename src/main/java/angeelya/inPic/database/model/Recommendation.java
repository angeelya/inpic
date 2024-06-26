package angeelya.inPic.database.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recommendation")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Recommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double grade;
    @JoinColumn(name = "user_id")
    @ManyToOne(optional = false)
    private User user;
    @JoinColumn(name = "image_id")
    @ManyToOne(optional = false)
    private Image image;

}
