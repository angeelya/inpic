package angeelya.inPic.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "saved_image")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SavedImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinColumn(name = "user_id")
    @ManyToOne(optional = false,cascade = CascadeType.ALL)
    private User user;
    @JoinColumn(name = "image_id")
    @ManyToOne(optional = false,cascade = CascadeType.ALL)
    private Image image;
}
