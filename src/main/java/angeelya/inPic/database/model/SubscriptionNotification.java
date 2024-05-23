package angeelya.inPic.database.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "subscription_notification")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="is_read")
    private Boolean isRead;
    @JoinColumn(name = "friend_id")
    @OneToOne(optional = false)
    private Friend friend;
}
