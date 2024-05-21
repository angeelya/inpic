package angeelya.inPic.database.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comment_notification")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="is_read")
    private boolean isRead;
    @JoinColumn(name = "comment_id")
    @OneToOne(optional = false)
    private Comment comment;
}
