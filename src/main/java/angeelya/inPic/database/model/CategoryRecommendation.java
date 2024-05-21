package angeelya.inPic.database.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "category_recommendation")
@Data
@Builder

@AllArgsConstructor
@NoArgsConstructor
public class CategoryRecommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double grade;
    @JoinColumn(name = "user_id")
    @ManyToOne(optional = false)
    private User user;
    @JoinColumn(name = "category_id")
    @ManyToOne(optional = false)
    private Category category;
}
