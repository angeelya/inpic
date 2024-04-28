package angeelya.inPic.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "like_notification")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LikeNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Boolean read;
    @JoinColumn(name = "like_id")
    @OneToOne(optional = false,cascade = CascadeType.ALL)
    private Like like;
}
