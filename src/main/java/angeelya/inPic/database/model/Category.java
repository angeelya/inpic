package angeelya.inPic.database.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "category")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String category;
    @JsonIgnore
    @OneToMany(mappedBy = "category",cascade = CascadeType.ALL)
    private List<CategoryRecommendation> categoryRecommendations;
    @JsonIgnore
    @OneToMany(mappedBy = "category",cascade = CascadeType.ALL)
    private List<Image> images;
}
