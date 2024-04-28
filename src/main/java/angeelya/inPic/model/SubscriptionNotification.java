package angeelya.inPic.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "subscription_notification")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Boolean read;
    @JoinColumn(name = "friend_id")
    @OneToOne(optional = false,cascade = CascadeType.ALL)
    private Friend friend;
}
