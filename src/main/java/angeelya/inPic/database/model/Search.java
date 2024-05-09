package angeelya.inPic.database.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "search")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Search {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;
    @JsonIgnore
    @JoinColumn(name = "user_id")
    @ManyToOne(optional = false,cascade = CascadeType.ALL)
    private User user;
}
