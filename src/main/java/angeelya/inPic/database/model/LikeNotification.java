package angeelya.inPic.database.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "like_notification")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LikeNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="is_read")
    private boolean isRead;
    @JoinColumn(name = "like_id")
    @OneToOne(optional = false,cascade = CascadeType.ALL)
    private Like like;
}
