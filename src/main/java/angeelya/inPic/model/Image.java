package angeelya.inPic.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "image")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String path;
    @Column(name = "img_name")
    private String imgName;
    private String name;
    private String description;
    @JoinColumn(name = "user_id")
    @ManyToOne(optional = false,cascade = CascadeType.ALL)
    private User user;
    @JoinColumn(name = "category_id")
    @ManyToOne(optional = false,cascade = CascadeType.ALL)
    private Category category;
    @ManyToMany(mappedBy = "images")
    private List<Album> albums;

}
