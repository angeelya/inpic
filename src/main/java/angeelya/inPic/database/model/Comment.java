package angeelya.inPic.database.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "comment")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;
    @JoinColumn(name = "user_id")
    @ManyToOne(optional = false)
    private User user;
    @JoinColumn(name = "image_id")
    @ManyToOne(optional = false)
    private Image image;
    @JsonIgnore
    @OneToOne(mappedBy = "comment",cascade = CascadeType.ALL)
    private CommentNotification commentNotification;
}
