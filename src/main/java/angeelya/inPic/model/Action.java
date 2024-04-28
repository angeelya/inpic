package angeelya.inPic.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "action")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Action {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double grade;
    @JoinColumn(name = "user_id")
    @ManyToOne(optional = false,cascade = CascadeType.ALL)
    private User user;
    @JoinColumn(name = "image_id")
    @ManyToOne(optional = false,cascade = CascadeType.ALL)
    private Image image;
}
