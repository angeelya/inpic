package angeelya.inPic.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "admin_notification")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="is_read")
    private boolean isRead;
    @JoinColumn(name = "user_id")
    @ManyToOne(optional = false,cascade = CascadeType.ALL)
    private User user;
    @JoinColumn(name = "deleted_image_id")
    @OneToOne(optional = false,cascade = CascadeType.ALL)
    private DeletedImage deletedImage;
}
