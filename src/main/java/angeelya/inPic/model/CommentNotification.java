package angeelya.inPic.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comment_notification")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="is_read")
    private boolean isRead;
    @JoinColumn(name = "comment_id")
    @OneToOne(optional = false,cascade = CascadeType.ALL)
    private Comment comment;
}
