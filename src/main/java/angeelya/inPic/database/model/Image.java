package angeelya.inPic.database.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "image")
@Data
@Builder
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
    @ManyToOne(optional = false)
    private User user;
    @JoinColumn(name = "category_id")
    @ManyToOne(optional = false)
    private Category category;
    @JsonIgnore
    @ManyToMany(mappedBy = "images")
    private List<Album> albums;
    @JsonIgnore
    @OneToMany(mappedBy = "image",cascade = CascadeType.ALL)
    private List<Like> like;
    @JsonIgnore
    @OneToMany(mappedBy = "image",cascade = CascadeType.ALL)
    private List<Action> actions;
    @JsonIgnore
    @OneToMany(mappedBy = "image",cascade = CascadeType.ALL)
    private List<Comment> comments;
    @JsonIgnore
    @OneToMany(mappedBy = "image",cascade = CascadeType.ALL)
    private List<Recommendation> recommendations;
    @JsonIgnore
    @OneToMany(mappedBy = "image",cascade = CascadeType.ALL)
    private List<SavedImage> savedImages;

}
