package angeelya.inPic.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String login;
    private String password;
    private String description;
    private String email;
    private String name;
    @Enumerated(EnumType.STRING)
    private Role role;
    @OneToOne(optional = false,mappedBy = "user")
    private UserImage userImage;
}
