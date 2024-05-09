package angeelya.inPic.database.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_image")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String path;
    private String name;
    @JsonIgnore
    @JoinColumn(name = "user_id")
    @OneToOne(optional = false,cascade = CascadeType.ALL)
    private User user;
}
